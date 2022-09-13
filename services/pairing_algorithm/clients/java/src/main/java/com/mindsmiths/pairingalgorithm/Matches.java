package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matches implements Serializable {
    private List<Match> allMatches;
}
