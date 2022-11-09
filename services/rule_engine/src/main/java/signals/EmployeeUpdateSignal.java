package signals;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.sdk.core.api.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateSignal extends Message {
    private String agentId;
    private Employee employee;
}