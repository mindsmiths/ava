package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindsmiths.pairingalgorithm.EmployeeAvailability;
import com.mindsmiths.pairingalgorithm.Match;
import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.pairingalgorithm.LunchCompatibilities;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.core.db.DataUtils;
import com.mindsmiths.sdk.core.db.EmitType;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.EmployeeProfile;
import models.CmLunchCycleStage;
import signals.AllEmployees;
import signals.EmployeeUpdateSignal;
import signals.SendMatchesSignal;
import signals.SendNoMatchesSignal;

import java.io.IOException;
import com.mindsmiths.sdk.utils.templating.Templating;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import java.util.Objects;
import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.ruleEngine.util.Log;
import java.io.FileOutputStream;  
import com.mindsmiths.emailAdapter.AttachmentData;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    private List<EmployeeAvailability> employeeAvailabilities = new ArrayList<>();
    private List<Match> allMatches = new ArrayList<>();
    private CmLunchCycleStage lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
    private Map<String, EmployeeProfile> employees = new HashMap<>();
    private Map<String, Map<String, Double>> employeeConnectionStrengths = new HashMap<>();
    private Map<String, List<String>> employeeMatchHistories = new HashMap<>();
    private LunchCompatibilities lunchCompatibilities;

    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

    public void addEmployeeAvailability(EmployeeAvailability employeeAvailability) {
        employeeAvailabilities.add(employeeAvailability);
    }

    public void clearEmployeeAvailabilities() {
        this.employeeAvailabilities = new ArrayList<>();
    }

    public void addOrUpdateEmployee(EmployeeUpdateSignal signal) {
        employees.put(signal.getFrom(), signal.getEmployee());
    }

    public void sendEmployeesToAva() {
        for (String address : employees.keySet()) {
            AllEmployees allEmployees = new AllEmployees(employees);
            send(address, allEmployees);
        }
    }

    public void generateMatches() {
        PairingAlgorithmAPI.generatePairs(
                new ArrayList<>(employeeAvailabilities),
                new HashMap<>(employeeConnectionStrengths),
                new HashMap<>(employeeMatchHistories));
    }


    public void addMatches(List<Match> allMatches) {
        this.allMatches = allMatches;
    }

    public void sendMatches() {
        List<String> matchedPeople = new ArrayList<>();
        for (String employeeKey : employees.keySet()) {
            for (Match m : allMatches) {
                if (employees.get(employeeKey).getId().equals(m.getFirst())) {
                    matchedPeople.add(m.getFirst());
                    break;
                }
                if (employees.get(employeeKey).getId().equals(m.getSecond())) {
                    matchedPeople.add(m.getSecond());
                    break;
                }
            }
        }
        for (String employeeKey : employees.keySet())
            if (!matchedPeople.contains(employees.get(employeeKey).getId()))
                send(employeeKey, new SendNoMatchesSignal());

        for (Match m : allMatches) {
            send(employeeToAvaId(m.getFirst()), new SendMatchesSignal(m.getSecond(), m.getDay()));
            send(employeeToAvaId(m.getSecond()), new SendMatchesSignal(m.getFirst(), m.getDay()));
        }
        for (Match m : allMatches)
            DataUtils.emit(new models.Match(m), EmitType.CREATE);

    }

    public String employeeToAvaId(String employeeId) {
        for (Map.Entry<String, EmployeeProfile> entry : employees.entrySet())
            if (entry.getValue().getId().equals(employeeId))
                return entry.getKey();
        return "";
    }

    public void statsEmail(String base64) throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(
                        "emailTemplates/StatEmailTemplate.html"))
                .readAllBytes());
        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", "Description1",
                "description2", "Description2",
                "image", "data:image/png;base64, " + base64 ));

        FileOutputStream outputStream = new FileOutputStream("email.html");
        byte[] strToBytes = htmlBody.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();

        //AttachmentData data = new AttachmentData();
        //data.setFileBytes(base64);
        SendEmailPayload email = new SendEmailPayload();
        email.setRecipients(List.of("ivan.dukic@mindsmiths.com"));
        email.setSubject("Stats email");
        email.setHtmlText(htmlBody);
        //email.setAttachments(List.of(data));
        EmailAdapterAPI.newEmail(email);
    }
}