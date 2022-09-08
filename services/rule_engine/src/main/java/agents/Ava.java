package agents;

import java.util.ArrayList;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.mindsmiths.ruleEngine.model.Agent;

import signals.DayChoiceSignal;

@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent { 
    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void sendData(ArrayList<Integer> freeDays) {
        send("CultureMaster", new DayChoiceSignal(freeDays));
    }
 
}
