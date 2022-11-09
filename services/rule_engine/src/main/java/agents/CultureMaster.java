package agents;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.pairingalgorithm.*;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.core.api.DataChangeType;
import com.mindsmiths.sdk.core.db.Database;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.CmLunchCycleStage;
import signals.EmployeeUpdateSignal;
import signals.SendMatchesSignal;
import signals.SendNoMatchesSignal;

import java.util.*;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    private List<EmployeeAvailability> employeeAvailabilities = new ArrayList<>();
    private CmLunchCycleStage lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
    private Map<String, String> agentToEmployeeMapping = new HashMap<>();
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

    public void addOrUpdateEmployee(String agentId, Employee employee) {
        agentToEmployeeMapping.put(agentId, employee.getId());
    }

    public void sendEmployeesToAva(EmployeeUpdateSignal signal) {
        for (String avaId : agentToEmployeeMapping.keySet())
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
            Database.emitChange(new models.Match(m), DataChangeType.CREATED);
        }
        for (String agentId : agentToEmployeeMapping.keySet())
            if (!matchedPeople.contains(agentId))
                send(agentId, new SendNoMatchesSignal());
    }
}