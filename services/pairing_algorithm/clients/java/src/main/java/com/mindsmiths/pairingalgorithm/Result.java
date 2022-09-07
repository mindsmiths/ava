package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {
    private ArrayList<FinalPairWithDays> result;
}
