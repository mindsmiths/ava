package signals;

import lombok.AllArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.pairingalgorithm.AgentAvailableDays;


@AllArgsConstructor
public class DayChoiceSignal extends Signal {
    AgentAvailableDays agentAvailableDays;    
}
