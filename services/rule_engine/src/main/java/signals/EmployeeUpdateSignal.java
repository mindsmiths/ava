package signals;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.sdk.core.api.Message;

import lombok.NoArgsConstructor;
import lombok.Data;

import models.EmployeeProfile;


@NoArgsConstructor
@Data
public class EmployeeUpdateSignal extends Message {
    private EmployeeProfile employeeProfile;
    private Employee employee;

    public EmployeeUpdateSignal(EmployeeProfile employee) {
        this.employee = employee;
        this.employeeProfile = new EmployeeProfile(
            employee.getFamiliarity(),
            employee.getId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail()
        );
    }
}