package agents;

import java.util.*;

import lombok.Data;

import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.utils.templating.Templating;

@Data
public class CultureMaster extends Agent {
    
    List<Map<String, List<Integer>>> freeDayList; // information about available days
    Map<String, Ava> agentInfo; // information about agents
    private Date lastEmailSentTime = new Date();
    private boolean pinged = false;

    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

}
