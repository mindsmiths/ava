package com.mindsmiths.pairingalgorithm;

import java.util.List;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.sdk.core.db.DataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DataModel(serviceName = "pairing_algorithm")
public class LunchCompatibilities extends Signal{
    private List<LunchCompatibilityEdge> edges;
}
