package agents;
import java.util.ArrayList;

import com.mindsmiths.ruleEngine.model.Agent;

import lombok.Data;
import lombok.ToString;
import signals.DayChoiceSignal;

@Data
@ToString

public class Ava extends Agent { 

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void sendData(ArrayList<Integer> freeDays) {
        send("CultureMaster", new DayChoiceSignal(freeDays));
    }
 
}
