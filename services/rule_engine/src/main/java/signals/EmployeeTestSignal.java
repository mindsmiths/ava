package signals;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.sdk.core.api.Message;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeTestSignal extends Message {
    public Employee employee;
}