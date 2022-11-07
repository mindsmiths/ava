package signals;

import com.mindsmiths.sdk.core.api.Message;

import lombok.NoArgsConstructor;
import lombok.Data;

import models.EmployeeProfile;


@NoArgsConstructor
@Data
public class EmployeeUpdateSignal extends Message {
    private EmployeeProfile employee;

    public EmployeeUpdateSignal(EmployeeProfile employee) {
        this.employee = new EmployeeProfile(
            employee.getFamiliarity(),
            employee.getId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail()
        );
    }
}