package agents;

import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.pairingalgorithm.AgentAvailableDays;
import com.mindsmiths.pairingalgorithm.FinalPairWithDays;
import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.Data;

@Data
public class CultureMaster extends Agent {
    List<AgentAvailableDays> pairData = new ArrayList<>();
    List<FinalPairWithDays> finalPairs = new ArrayList<>();
    Date lastGeneratePairsTime;
    List<String> avaIDs = new ArrayList<>();
    boolean sent = false; 
    Ava ava;

    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

    public void addNewID(String newId) {
        avaIDs.add(newId);
    }

    public void addFinalPairs(ArrayList<FinalPairWithDays> finalPairs) {
        this.finalPairs = finalPairs;
    }

    public void generatePairs() {
        // hardcoding for testing ----------------------
        List<Boolean> mon = List.of(true,false,false,false,false);
        List<Boolean> fri = List.of(false,false,false,false,true);
        List<Boolean> thu_fri = List.of(false,false,false,true,true);
        pairData.add(new AgentAvailableDays("Marko", mon));
        pairData.add(new AgentAvailableDays("Misko", thu_fri));
        pairData.add(new AgentAvailableDays("Joza", fri));
        pairData.add(new AgentAvailableDays("Eugen", fri));
        pairData.add(new AgentAvailableDays("Stef", mon));
        //----------------------------------------------
        if(!sent) {
            PairingAlgorithmAPI.generatePairs(new ArrayList<>(pairData));
            sent = true;
        }
        
    }

    public void sendAvailabilityData(){

    }

}
