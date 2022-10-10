package signals;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.mindsmiths.sdk.core.api.Signal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeConnectionStrengthsSignal extends Signal {
    private String employeeId;
    private Map<String, Double> connectionStrengths = new HashMap<>();
}
