package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.core.api.Message;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SendMatchesSignal extends Message { 
    private String match;
    private Days matchDay;
}
