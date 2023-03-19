package signals;

import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.core.api.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SendMatchesSignal extends Message {
    private String match;
    private Days matchDay;
}
