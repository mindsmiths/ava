package signals;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.core.api.Signal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SendMatchesSignal extends Signal { 
    private String matchName;
    private Days matchDay;
    private Map<String, String> matchAnswers = new HashMap<String, String>();
}
