package com.mindsmiths.pairingalgorithm;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAvailability {
    private String employeeId;
    private List<Days> availableDays = new ArrayList<>();
}
