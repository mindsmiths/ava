package signals;

import com.mindsmiths.sdk.core.api.Signal;

import java.util.HashMap;
import java.util.Map;

import models.EmployeeProfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AllEmployees extends Signal {
    private Map<String, EmployeeProfile> allEmployees = new HashMap<>();
    private int silosCount;
    private String silosRisk;
}
