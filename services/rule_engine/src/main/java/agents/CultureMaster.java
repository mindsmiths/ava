package agents;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.CmLunchCycleStage;

import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.pairingalgorithm.AvaAvailability;
import com.mindsmiths.pairingalgorithm.Match;

import signals.EmployeeUpdateSignal;
import signals.AllEmployees;
import signals.SendMatchesSignal;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    private List<AvaAvailability> avaAvailabilities = new ArrayList<>();
    private List<Match> allMatches = new ArrayList<>();
    private CmLunchCycleStage lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
    private Map<String, Employee> employees = new HashMap<>();

    public static String ID = "CULTURE_MASTER";
    public CultureMaster() {
        id = ID;
    }

    public void addAvaAvailability(AvaAvailability avaAvailability) {
        avaAvailabilities.add(avaAvailability);
    }

    public void clearAvaAvailabilities() {
        this.avaAvailabilities = new ArrayList<>();
    }

    public void generateMatches() {
            PairingAlgorithmAPI.generatePairs(new ArrayList<>(avaAvailabilities));
    }

    public void addMatches(List<Match> allMatches) {
        this.allMatches = allMatches;
    }

    public void sendMatches() {
        for(Employee employee : employees.values()) {
            for(Match m: allMatches) {
                if(employee.getId().equals(m.getFirst())) {
                    send(employee.getId(), new SendMatchesSignal(employees.get(m.getSecond()).getFirstName() + " " +
                                                                 employees.get(m.getSecond()).getLastName(),
                                                                 m.getDay()));
                    break;
                }
                if(employee.getId().equals(m.getSecond())) {
                    send(employee.getId(), new SendMatchesSignal(employees.get(m.getFirst()).getFirstName() + " " +
                                                                 employees.get(m.getFirst()).getLastName(),
                                                                 m.getDay()));
                    break;
                }
            }
        }
    }
    
    public void addOrUpdateEmployee(EmployeeUpdateSignal signal){
        employees.put(signal.getFrom(), signal.getEmployee());
    }

    public void sendEmployeesToAva() {
        for (String address : employees.keySet()) {
            AllEmployees allEmployees = new AllEmployees(employees);
            send(address, allEmployees);
        }
    }

}
