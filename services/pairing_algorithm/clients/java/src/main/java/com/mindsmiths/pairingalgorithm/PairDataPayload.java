package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PairDataPayload implements Serializable {
    private ArrayList<AgentAvailableDays> pairData;
}
