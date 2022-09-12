package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.Data;

@Data
public class CultureMaster extends Agent {
    private List<Map<String, List<Integer>>> freeDayList; // information about available days
    private Map<String, Ava> agentInfo; // information about agents
    private Ava ava;
    private Map<String, List<String>> avasInfo = new HashMap<>();

    public static String ID = "CULTURE_MASTER";
    public CultureMaster() {
        id = ID;
    }

    public void addOrUpdateAvaData(String id, String firstName, String lastName) {
        List<String> pair = new ArrayList<>();
        if(!avasInfo.containsKey(id)){
        pair.add(firstName);
        pair.add(lastName);
        avasInfo.put(id, pair);
    }else{
        pair.add(firstName);
        pair.add(lastName);
        avasInfo.replace(id, pair);
    }

    }

}
