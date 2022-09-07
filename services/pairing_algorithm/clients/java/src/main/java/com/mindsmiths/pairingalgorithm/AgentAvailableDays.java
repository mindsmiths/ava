package com.mindsmiths.pairingalgorithm;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AgentAvailableDays {
    private String agentId;
    private List<Boolean> availableDays = new ArrayList<>();

    public AgentAvailableDays(String agentId, List<Boolean> availableDaysTest) {
        this.agentId = agentId;
        this.availableDays = availableDaysTest;
    }

}
