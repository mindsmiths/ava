package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.Data;

@Data
public class CultureMaster extends Agent {
    List<Map<String, List<Integer>>> freeDayList; // information about available days
    Map<String, Ava> agentInfo; // information about agents
    Ava ava;

    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

}
