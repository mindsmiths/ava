package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaAvailabilitiesPayload implements Serializable {
    private List<AvaAvailability> avaAvailabilities;
}
