package agents;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.pairingalgorithm.AgentAvailableDays;

import signals.DayChoiceSignal;


@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent {

    AgentAvailableDays agentAvailableDays;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void sendData(AgentAvailableDays agentAvailableDays) {
        send("CultureMaster", new DayChoiceSignal(agentAvailableDays));
    }

}
