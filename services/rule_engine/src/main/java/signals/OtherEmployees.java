package signals;

import java.util.HashMap;
import java.util.Map;

import com.mindsmiths.sdk.core.api.Signal;

import models.EmployeeProfile;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OtherEmployees extends Signal {
    Map<String, EmployeeProfile> otherEmployees = new HashMap<>();

    public String employeeToAvaId(String employeeId) {
        for (Map.Entry<String, EmployeeProfile> employee : this.otherEmployees.entrySet())
            if (employee.getValue().getId().equals(employeeId))
                return employee.getKey();
        return "";
    } 
}
