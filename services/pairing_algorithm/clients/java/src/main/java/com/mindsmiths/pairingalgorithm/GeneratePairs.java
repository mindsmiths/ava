package com.mindsmiths.pairingalgorithm;

import com.mindsmiths.sdk.core.api.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePairs extends Message {
    private List<EmployeeAvailability> employeeAvailabilities;
    private Map<String, Map<String, Double>> employeeConnectionStrengths;
}
