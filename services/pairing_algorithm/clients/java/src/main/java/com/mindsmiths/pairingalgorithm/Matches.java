package com.mindsmiths.pairingalgorithm;

import java.util.List;

import com.mindsmiths.sdk.core.api.Reply;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matches extends Reply {
    private List<Match> allMatches;
}
