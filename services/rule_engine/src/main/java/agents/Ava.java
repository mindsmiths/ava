package agents;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.Screen;
import com.mindsmiths.armory.event.Submit;
import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.emailAdapter.NewEmail;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.Neuron;
import models.OnboardingStage;
import signals.EmployeeUpdateSignal;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Ava extends Agent {
    public static final double CONNECTION_NEURON_CAPACITY = 100;
    public static final double CONNECTION_NEURON_RESISTANCE = 0.05;
    private OnboardingStage onboardingStage;
    private Map<String, String> lunchDeclineReasons = new HashMap<>();
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
        connectionStrengths.putIfAbsent(employeeAvaId, new Neuron(CONNECTION_NEURON_RESISTANCE, CONNECTION_NEURON_CAPACITY));
        return this.connectionStrengths.get(employeeAvaId);
    }

    public void chargeConnectionNeurons(Submit signal) {
        signal.getParams().fieldNames().forEachRemaining(paramId -> {
            if (paramId.startsWith("answers"))
                for (String em : signal.getParamAsList(paramId, String.class))
                    familiarity.put(em, familiarity.getOrDefault(em, 0.0) + 1.0);
        });

        for (Map.Entry<String, Double> entry : familiarity.entrySet())
            this.getConnectionNeuron(entry.getKey()).charge(entry.getValue() * 30);
    }

    public void decayConnectionNeurons() {
        for (String avaId : this.connectionStrengths.keySet()) {
            long daysPassed = ChronoUnit.DAYS.between(
                    getConnectionNeuron(avaId).getLastUpdatedAt(), Utils.now());
            getConnectionNeuron(avaId).decay(daysPassed);
        }
    }

    public void chargeAfterMatch(String employeeAvaId) {
        getConnectionNeuron(employeeAvaId).charge(30.);
    }

    public void updateEmployeeData(Employee employee) {
        setConnection("email", employee.getEmail());
        send(CultureMaster.ID, new EmployeeUpdateSignal(id, employee));
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


    public Map<String, String> createOtherEmployeeNames() {
        Map<String, String> otherEmployeeNames = new HashMap<>();
        for (Employee employee : otherEmployees.values().stream().filter(Employee::getActive).toList())
            otherEmployeeNames.put(employee.getId(), String.join(" ", employee.getFirstName(), employee.getLastName()));
        return otherEmployeeNames;
    }
}
