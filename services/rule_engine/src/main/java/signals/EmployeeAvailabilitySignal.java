package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.sdk.core.api.Message;
import com.mindsmiths.pairingalgorithm.EmployeeAvailability;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAvailabilitySignal extends Message {
    private EmployeeAvailability employeeAvailability;
}
