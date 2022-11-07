package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.List;

import com.mindsmiths.sdk.core.db.DataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DataModel(emit = true)
public class LunchCompatibilities implements Serializable{
    private List<LunchCompatibilityEdge> edges;
}
