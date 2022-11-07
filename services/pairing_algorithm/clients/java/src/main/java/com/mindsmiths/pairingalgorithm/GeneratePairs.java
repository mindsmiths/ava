package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.*;

import com.mindsmiths.sdk.core.api.Message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePairs extends Message {
    private List<EmployeeAvailability> employeeAvailabilities;
    private Map<String, Map<String, Double>> employeeConnectionStrengths;
}
