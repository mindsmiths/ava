package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.pairingalgorithm.EmployeeAvailability;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAvailabilitySignal extends Signal {
    private EmployeeAvailability employeeAvailability;
}
