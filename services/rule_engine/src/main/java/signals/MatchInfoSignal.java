package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.core.api.Signal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchInfoSignal extends Signal {
    private String matchName;
    private Days matchDay;
}
