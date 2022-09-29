package com.mindsmiths.pairingalgorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LunchCompatibilityEdge {
    private String first;
    private String second;
    private float edgeWeight;
}
