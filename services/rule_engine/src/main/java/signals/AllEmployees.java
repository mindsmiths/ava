package signals;

import com.mindsmiths.sdk.core.api.Signal;

import java.util.HashMap;
import java.util.Map;

import models.EmployeeProfile;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AllEmployees extends Signal {
    private Map<String, EmployeeProfile> allEmployees = new HashMap<>();
}
