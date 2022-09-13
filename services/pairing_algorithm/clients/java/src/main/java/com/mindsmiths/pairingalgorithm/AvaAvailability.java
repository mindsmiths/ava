package com.mindsmiths.pairingalgorithm;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaAvailability {
    private String agentId;
    private List<Days> availableDays = new ArrayList<>();
}
