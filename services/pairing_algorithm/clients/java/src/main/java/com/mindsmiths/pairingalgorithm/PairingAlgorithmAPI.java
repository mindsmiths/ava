package com.mindsmiths.pairingalgorithm;

import java.util.*;

public class PairingAlgorithmAPI {

    public static void generatePairs(List<EmployeeAvailability> employeeAvailabilities, Map<String, Map<String, Double>> employeeConnectionStrengths) {                             
        new GeneratePairs(employeeAvailabilities, employeeConnectionStrengths).send("pairing_algorithm");; 
    }
}