package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.Data;

@Data
public class CultureMaster extends Agent {
    List<Map<String, List<Integer>>> freeDayList; // information about available days
    Map<String, Ava> agentInfo; // information about agents

    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

    Agent agent;
    // saving a list of maps containing ID of a Agent from specific person and list of 0 and 1 depending on choice of the users available days in a week
}
