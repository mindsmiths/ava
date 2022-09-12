package signals;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.employeeManager.employees.Employee;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EmployeeUpdateSignal extends Signal {
    private Employee employee;

    public EmployeeUpdateSignal (Employee employee){
        this.employee = new Employee(employee.getId(), employee.getFirstName(), employee.getLastName(), null, null);
    }
}
