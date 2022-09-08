package agents;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mongodb.connection.ConnectionId;

import java.util.ArrayList;
import java.util.List;

import com.mindsmiths.pairingalgorithm.AgentAvailableDays;

@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent {
    private List<Boolean> availableDays = new ArrayList<>();
    private AgentAvailableDays agentAvailableDays;
    private String matchName;
    private int matchDay;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    //---------TESTING----------------------
    public void randomizeAvailableDays() {
        this.availableDays = new ArrayList<>();
        for(int i=0; i<5; i++) {
            availableDays.add(Math.random() < 0.5 ? true : false);
        }
    }

    public void updateAgentAvailableDays() {
        randomizeAvailableDays();
        this.agentAvailableDays = new AgentAvailableDays(this.id, availableDays);
    }
    //-------------------------------------

}
