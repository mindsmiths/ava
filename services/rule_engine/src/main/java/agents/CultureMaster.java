package agents;

import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.pairingalgorithm.AgentAvailableDays;
import com.mindsmiths.pairingalgorithm.FinalPairWithDays;
import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.Data;
import signals.MatchInfoSignal;

@Data
public class CultureMaster extends Agent {
    private List<AgentAvailableDays> availabilityList = new ArrayList<>();
    private List<FinalPairWithDays> finalPairs = new ArrayList<>();
    private Date lastRequestDataTime = new Date();
    private List<String> avaIDs = new ArrayList<>();
    private boolean sentGenerate = false; 
    private boolean sentCollect = false; 
    private boolean canGenerate = false; 
    private boolean sendGenerated = false; 
    private Ava ava;
    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

    public void addNewAva(String newAva) {
        avaIDs.add(newAva);
    }

    public void addAgentAvailableDays(AgentAvailableDays agentAvailableDays) {
        availabilityList.add(agentAvailableDays);
    }

    public void clearAvailabilityList() {
        this.availabilityList = new ArrayList<>();
    }

    public void generatePairs() {
        if(!sentGenerate) {
            PairingAlgorithmAPI.generatePairs(new ArrayList<>(availabilityList));
            sentGenerate = true;
        }  
    }

    public void addFinalPairs(ArrayList<FinalPairWithDays> finalPairs) {
        this.finalPairs = finalPairs;
    }

    public void giveMatchInfo() {
        for(String ava : avaIDs) {
            for(FinalPairWithDays fp: finalPairs) {
                if(ava.equals(fp.getPerson1())) {
                    send(ava, new MatchInfoSignal(fp.getPerson2(), fp.getDay()));
                    break;
                }
                if(ava.equals(fp.getPerson2())) {
                    send(ava, new MatchInfoSignal(fp.getPerson1(), fp.getDay()));
                    break;
                }
            }
        }
    }
}
