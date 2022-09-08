package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.pairingalgorithm.AgentAvailableDays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendAvailableDays extends Signal {
    AgentAvailableDays agentAvailableDays;
}
