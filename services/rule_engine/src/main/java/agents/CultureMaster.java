package agents;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.pairingalgorithm.*;
import com.mindsmiths.ruleEngine.model.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.CmLunchCycleStage;
import signals.DeleteLunchCycleDataSignal;
import signals.EmployeeUpdateSignal;
import signals.SendMatchesSignal;

import java.util.*;
import java.util.Map.Entry;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    public static String ID = "CULTURE_MASTER";
    private List<EmployeeAvailability> employeeAvailabilities = new ArrayList<>();
    private CmLunchCycleStage lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
    private Map<String, Employee> employees = new HashMap<>();
    public Map<String, Map<String, Double>> employeeConnectionStrengths = new HashMap<>();
    private LunchCompatibilities lunchCompatibilities;

    private boolean pairingInterval;

    public CultureMaster() {
        id = ID;
    }

    public void addEmployeeAvailability(EmployeeAvailability employeeAvailability) {
        employeeAvailabilities.add(employeeAvailability);
    }

    public List<String> activeEmployees() {
        return employees.values().stream().filter(Employee::getActive).map(Employee::getId).toList();
    }

    public void addOrUpdateEmployee(EmployeeUpdateSignal signal) {
        String agentId = signal.getAgentId();
        if (!employees.containsKey(agentId)) sendNewAvaAllEmployees(agentId);

        employees.put(agentId, signal.getEmployee());

        sendEmployeeToAvas(signal);
    }

    public void sendNewAvaAllEmployees(String agentId) {
        for (Entry<String, Employee> otherEmployee : employees.entrySet())
            send(agentId, new EmployeeUpdateSignal(otherEmployee.getKey(), otherEmployee.getValue()));
    }

    public void sendEmployeeToAvas(EmployeeUpdateSignal signal) {
        for (String avaId : employees.keySet())
            if (!Objects.equals(avaId, signal.getAgentId()))
                send(avaId, signal);
    }

    public void generateMatches() {
        PairingAlgorithmAPI.generatePairs(new ArrayList<>(employeeAvailabilities), new HashMap<>(employeeConnectionStrengths));
    }


    public void sendMatches(List<Match> matches) {
        Set<String> matchedPeople = new HashSet<>();
        for (Match m : matches) {
            String firstMatch = m.getFirst(), secondMatch = m.getSecond();
            Days matchDay = m.getDay();
            matchedPeople.add(firstMatch);
            matchedPeople.add(secondMatch);
            send(firstMatch, new SendMatchesSignal(secondMatch, matchDay));
            send(secondMatch, new SendMatchesSignal(firstMatch, matchDay));
        }
        for (String agentId : employees.keySet())
            if (!matchedPeople.contains(agentId))
                send(agentId, new SendMatchesSignal());
    }

    public void resetLunchCycle() {
        lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
        employeeAvailabilities = new ArrayList<>();
        employeeConnectionStrengths = new HashMap<>();
    }

    public void deleteLunchCycleDataOnAvas() {
        for (String avaId : employees.keySet())
            send(avaId, new DeleteLunchCycleDataSignal());
        resetLunchCycle();
    }
}