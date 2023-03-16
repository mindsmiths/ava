package signals;

import com.mindsmiths.sdk.core.api.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeConnectionStrengthsSignal extends Message {
    private Map<String, Double> connectionStrengths = new HashMap<>();
}
