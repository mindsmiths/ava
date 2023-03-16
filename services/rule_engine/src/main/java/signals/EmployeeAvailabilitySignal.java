package signals;

import com.mindsmiths.pairingalgorithm.EmployeeAvailability;
import com.mindsmiths.sdk.core.api.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAvailabilitySignal extends Message {
    private EmployeeAvailability employeeAvailability;
}
