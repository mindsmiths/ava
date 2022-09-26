package signals;

import java.util.Map;
import java.util.HashMap;

import com.mindsmiths.sdk.core.api.Signal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionsStrengthSignal extends Signal {
    private String employeeId;
    private Map<String, Double> connectionStrengths = new HashMap<>();
}
