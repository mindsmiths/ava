package signals;

import com.mindsmiths.pairingalgorithm.EmployeeAvailability;
import com.mindsmiths.sdk.core.api.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PairingRequest extends Message {
    private EmployeeAvailability employeeAvailability;
    private Map<String, Double> connectionStrengths;
}
