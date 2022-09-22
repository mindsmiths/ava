package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.pairingalgorithm.AvaAvailability;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaAvailabilitySignal extends Signal {
    AvaAvailability avaAvailability;

}
