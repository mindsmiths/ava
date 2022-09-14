package agents;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.CultureMasterWeeklyStage;

import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.pairingalgorithm.AvaAvailability;
import com.mindsmiths.pairingalgorithm.Match;

import signals.EmployeeUpdateSignal;
import signals.MatchInfoSignal;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    private List<AvaAvailability> avaAvailabilities = new ArrayList<>();
    private List<Match> allMatches = new ArrayList<>();
    private List<String> avaIDs = new ArrayList<>();
    private CultureMasterWeeklyStage weeklyStage = CultureMasterWeeklyStage.COLLECT_AVA_AVAILABILITIES;
    private List<Map<String, List<Integer>>> freeDays; // information about available days
    private Map<String, Employee> employees = new HashMap<>();

    public static String ID = "CULTURE_MASTER";
    public CultureMaster() {
        id = ID;
    }

    public void addNewAva(String newAva) {
        avaIDs.add(newAva);
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

    public void sendMatchInfo() {
        for(String ava : avaIDs) {
            for(Match m: allMatches) {
                if(ava.equals(m.getFirst())) {
                    send(ava, new MatchInfoSignal(m.getSecond(), m.getDay()));
                    break;
                }
                if(ava.equals(m.getSecond())) {
                    send(ava, new MatchInfoSignal(m.getFirst(), m.getDay()));
                    break;
                }
            }
        }
    }
    
    public void addOrUpdateEmployee(EmployeeUpdateSignal signal){
        employees.put(signal.getFrom(), signal.getEmployee());
    }
}
