package signals;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.sdk.core.api.Message;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class EmployeeUpdateSignal extends Message {
    private Employee employee;

    public EmployeeUpdateSignal(Employee employee) {
        this.employee = employee;
    }
}