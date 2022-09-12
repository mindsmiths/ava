package agents;

import java.util.*;

import lombok.Data;

import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.pairingalgorithm.AvaAvailability;
import com.mindsmiths.pairingalgorithm.Match;
import com.mindsmiths.ruleEngine.model.Agent;

import signals.MatchInfoSignal;

@Data
public class CultureMaster extends Agent {
    private List<AvaAvailability> avaAvailabilities = new ArrayList<>();
    private List<Match> allMatches = new ArrayList<>();
    private List<String> avaIDs = new ArrayList<>();
    private CultureMasterWeeklyStage weeklyStage = CultureMasterWeeklyStage.COLLECT_AVA_AVAILABILITIES;
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
}
