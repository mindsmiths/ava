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
import com.mindsmiths.sdk.core.api.DataChangeType;
import com.mindsmiths.sdk.core.api.Message;
import com.mindsmiths.sdk.core.db.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.EmployeeProfile;
import models.CmLunchCycleStage;
import signals.AllEmployees;
import signals.EmployeeUpdateSignal;
import signals.SendMatchesSignal;
import signals.SendNoMatchesSignal;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    private List<EmployeeAvailability> employeeAvailabilities = new ArrayList<>();
    private List<Match> allMatches = new ArrayList<>();
    private CmLunchCycleStage lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
    private Map<String, EmployeeProfile> employees = new HashMap<>();
    private Map<String, Map<String, Double>> employeeConnectionStrengths = new HashMap<>();
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

    public void addNewEmployee(String id,EmployeeProfile employeeProfile) {
        employees.put(id, employeeProfile);
    }

    public void addOrUpdateEmployee(EmployeeUpdateSignal signal) {
        employees.put(signal.getFrom(), signal.getEmployeeProfile());
    }

    public void sendEmployeesToAva() {
        for (String avaId : employees.keySet()) {
            Message allEmployees = new AllEmployees(employees);
            send(avaId, allEmployees);
        }
    }

    public void generateMatches() {
        PairingAlgorithmAPI.generatePairs(new ArrayList<>(employeeAvailabilities), new HashMap<>(employeeConnectionStrengths));
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
            Database.emitChange(new models.Match(m), DataChangeType.CREATED);
         

    }

    public String employeeToAvaId(String employeeId) {
        for (Map.Entry<String, EmployeeProfile> entry : employees.entrySet())
            if (entry.getValue().getId().equals(employeeId))
                return entry.getKey();
        return "";
    }
}