package agents;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mongodb.connection.ConnectionId;
import com.mindsmiths.pairingalgorithm.AgentAvailableDays;

import signals.DayChoiceSignal;
import signals.SendIdSignal;


@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent {

    AgentAvailableDays agentAvailableDays;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void sendId(String id) {
        send("CultureMaster", new SendIdSignal(id));
    }

    public void sendData(AgentAvailableDays agentAvailableDays) {
        send("CultureMaster", new DayChoiceSignal(agentAvailableDays));
    }

}
