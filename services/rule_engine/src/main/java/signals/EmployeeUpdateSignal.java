package signals;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.employeeManager.employees.Employee;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EmployeeUpdateSignal extends Signal {
    private String id;
    private Employee employee;

    public EmployeeUpdateSignal (String id, Employee employee){
        this.id = id;
        this.employee = new Employee(null, employee.getFirstName(), employee.getLastName(), null, null);
    }
}
