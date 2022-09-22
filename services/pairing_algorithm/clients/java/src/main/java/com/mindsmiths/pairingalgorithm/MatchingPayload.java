package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchingPayload implements Serializable {
    private List<EmployeeAvailability> employeeAvailabilities;
    private Map<String, Map<String, Double>> employeeConnectionStrengths;
}
