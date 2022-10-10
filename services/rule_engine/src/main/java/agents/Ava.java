package agents;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.ruleEngine.util.DateUtil;
import com.mindsmiths.ruleEngine.util.Log;

import models.*;
import signals.SendMatchesSignal;
import utils.EventTracking;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.IOException;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Ava extends Agent {
    private List<Days> availableDays = new ArrayList<>();
    private String match;
    private Days matchDay;
    private Date availableDaysEmailLastSentAt;
    private Date matchedWithEmailSentAt;
    private AvaLunchCycleStage lunchCycleStage;
    private OnboardingStage onboardingStage;
    private MonthlyCoreStage monthlyCoreStage;
    private LunchReminderStage lunchReminderStage;
    private Map<String, EmployeeProfile> otherEmployees;
    private boolean manualTrigger;
    private boolean workingHours;
    private boolean availabilityInterval;
    private List<String> lunchDeclineReasons = new ArrayList<>();
    private Map<String, Neuron> connectionStrengths = new HashMap<>();
    public static final double CONNECTION_NEURON_CAPACITY = 100;
    public static final double CONNECTION_NEURON_RESISTANCE = 0.05;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void showScreen(BaseTemplate screen) {
        ArmoryAPI.showScreen(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, Map<String, BaseTemplate> screens) {
        ArmoryAPI.showScreens(getConnection("armory"), firstScreenId, screens);
    }

    public void sendEmail(SendEmailPayload email) throws IOException {
        EmailAdapterAPI.newEmail(email);
    }

    public void addConnectionStrengths() {
        for (String avaId : this.otherEmployees.keySet())
            if (!connectionStrengths.containsKey(otherEmployees.get(avaId).getId())) {
                connectionStrengths.put(otherEmployees.get(avaId).getId(),
                        new Neuron(CONNECTION_NEURON_RESISTANCE, CONNECTION_NEURON_CAPACITY));
            }
    }

    public Neuron getConnectionNeuron(String employeeId) {
        return this.connectionStrengths.get(employeeId);
    }

    public void chargeConnectionNeurons(EmployeeProfile employeeProfile) {
        for (Map.Entry<String, Double> entry : employeeProfile.getFamiliarity().entrySet())
            this.getConnectionNeuron(entry.getKey()).charge(entry.getValue() * 30);
            //remove
            Log.info("aaaa charging by " + this.connectionStrengths);
    }

    public void decayConnectionNeurons() {
        for (String avaId : this.otherEmployees.keySet()) {
            // remove 
            Log.info("Decaying SPECIFIC neuron with employee id: " + otherEmployees.get(avaId).getId());
            long daysPassed = ChronoUnit.DAYS.between(
                    getConnectionNeuron(otherEmployees.get(avaId).getId()).getLastUpdatedAt().toInstant(),
                    new Date().toInstant());
            getConnectionNeuron(otherEmployees.get(avaId).getId()).decay(daysPassed);
        }
    }

    public void chargeAfterMatch(String employeeId) {
        getConnectionNeuron(employeeId).charge(30.);
    }

    public Map<String, Double> getConnectionStrengthAsValue() {
        Map<String, Double> m = new HashMap<>();
        for (Map.Entry<String, Neuron> entry : connectionStrengths.entrySet())
            m.put(entry.getKey(), entry.getValue().getValue());
        return m;
    }

    public boolean anyCronSatisfied(Date timestamp, String timezone, String... crons) throws ParseException {
        for (String cron : crons)
            if (DateUtil.evaluateCronExpression(cron, timestamp, timezone))
                return true;
        return false;
    }

    public String employeeToAvaId(String employeeId) {
        for (Map.Entry<String, EmployeeProfile> entry : otherEmployees.entrySet())
            if (entry.getValue().getId().equals(employeeId))
                return entry.getKey();
        return "";
    }

    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for (String day : availableDaysStr)
            this.availableDays.add(Days.valueOf(day));
    }

    public void printMatchInfo(EmployeeProfile employee, SendMatchesSignal signal) {
        for (Map.Entry<String, EmployeeProfile> entry : otherEmployees.entrySet()) {
            if (entry.getValue().getId().equals(signal.getMatch())) {
                Log.info("I'm " + employee.getFullName() + " my match is " + entry.getValue().getFullName() + " on "
                        + signal.getMatchDay());
                break;
            }
        }
    }

    public Map<String, String> createOtherEmployeeNames() {
        Map<String, String> otherEmployeeNames = new HashMap<>();
        for (EmployeeProfile employee : otherEmployees.values())
            otherEmployeeNames.put(employee.getFullName(), employee.getId());

        return otherEmployeeNames;
    }

    public void identify() {
        EventTracking.identify(id, new HashMap<>() {
            {
                put("agentType", getClass().getSimpleName());
            }
        });
    }

    public void logEvent(String event) {
        EventTracking.capture(id, event);
    }

    public void logEvent(String event, Map<String, Object> properties) {
        EventTracking.capture(id, event, properties);
    }
}