package signals;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.sdk.core.api.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AllEmployees extends Message {
    private Map<String, Employee> allEmployees = new HashMap<>();
}
