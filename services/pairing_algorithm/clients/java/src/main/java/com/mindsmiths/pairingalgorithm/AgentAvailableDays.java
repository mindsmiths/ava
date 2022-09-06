package com.mindsmiths.pairingalgorithm;

import java.util.ArrayList;

import lombok.Data;

@Data
public class AgentAvailableDays {
    private String agentId;
    private ArrayList<Boolean> availableDays;

}
