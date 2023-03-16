package agents;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.Screen;
import com.mindsmiths.armory.event.Submit;
import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.emailAdapter.NewEmail;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.ruleEngine.model.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.Neuron;
import models.OnboardingStage;
import utils.EventTracking;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mindsmiths.ruleEngine.util.DateUtil.evaluateCronExpression;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Ava extends Agent {
    public static final double CONNECTION_NEURON_CAPACITY = 100;
    public static final double CONNECTION_NEURON_RESISTANCE = 0.05;
    private OnboardingStage onboardingStage;
    private Map<String, LocalDateTime> lunchDeclineReasons = new HashMap<>();
    private Map<String, Neuron> connectionStrengths = new HashMap<>();
    private Map<String, Double> familiarity = new HashMap<>();
    private Map<String, Employee> otherEmployees = new HashMap<>();
    // Metadata
    private boolean onboarded;
    private boolean workingHours;
    private boolean availabilityInterval;

    public void showScreen(Screen screen) {
        ArmoryAPI.show(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, List<Screen> screens) {
        ArmoryAPI.show(getConnection("armory"), firstScreenId, screens);
    }

    public void sendEmail(NewEmail email) {
        EmailAdapterAPI.newEmail(email);
    }

    public Neuron getConnectionNeuron(String employeeAvaId) {
        connectionStrengths.putIfAbsent(employeeAvaId,
                new Neuron(CONNECTION_NEURON_RESISTANCE, CONNECTION_NEURON_CAPACITY));
        return this.connectionStrengths.get(employeeAvaId);
    }

    public void chargeConnectionNeurons(Submit signal) {
        for (String paramId : signal.getParams().keySet())
            if (paramId.startsWith("answers"))
                for (String em : (List<String>) signal.getParam(paramId))
                    familiarity.put(em, familiarity.getOrDefault(em, 0.0) + 1.0);

        for (Map.Entry<String, Double> entry : familiarity.entrySet())
            this.getConnectionNeuron(entry.getKey()).charge(entry.getValue() * 30);
    }

    public void decayConnectionNeurons() {
        for (String avaId : this.connectionStrengths.keySet()) {
            long daysPassed = ChronoUnit.DAYS.between(
                    getConnectionNeuron(avaId).getLastUpdatedAt().toInstant(ZoneOffset.UTC),
                    new Date().toInstant());
            getConnectionNeuron(avaId).decay(daysPassed);
        }
    }

    public void chargeAfterMatch(String employeeAvaId) {
        getConnectionNeuron(employeeAvaId).charge(30.);
    }

    public void addOrUpdateEmployee(String agentId, Employee employee) {
        otherEmployees.put(agentId, employee);
        familiarity.putIfAbsent(agentId, 0.0);
        connectionStrengths.putIfAbsent(agentId, new Neuron(CONNECTION_NEURON_RESISTANCE, CONNECTION_NEURON_CAPACITY));
    }

    public Map<String, Double> getConnectionStrengthAsValue() {
        Map<String, Double> m = new HashMap<>();
        for (Map.Entry<String, Neuron> entry : connectionStrengths.entrySet())
            m.put(entry.getKey(), entry.getValue().getValue());
        return m;
    }

    public boolean anyCronSatisfied(LocalDateTime timestamp, String timezone, String... crons) {
        for (String cron : crons)
            if (evaluateCronExpression(cron, timestamp, timezone))
                return true;
        return false;
    }

    public Map<String, String> createOtherEmployeeNames() {
        Map<String, String> otherEmployeeNames = new HashMap<>();
        for (Employee employee : otherEmployees.values())
            otherEmployeeNames.put(String.join(" ", employee.getFirstName(), employee.getLastName()), employee.getId());

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