package com.mindsmiths.pairingalgorithm;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaAvailability {
    private String agentId;
    private Set<Days> availableDays = new HashSet<>();
}
