package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.*;

import com.mindsmiths.sdk.core.api.BaseMessage;
import com.mindsmiths.sdk.core.api.CallbackResult;
import com.mindsmiths.sdk.messaging.Messaging;


public class PairingAlgorithmAPI {
    private static final String topic = Messaging.getInputTopicName("pairing_algorithm");

    public static void generatePairs(List<EmployeeAvailability> employeeAvailabilities, Map<String, Map<String, Double>> employeeConnectionStrengths) {
        Serializable payload = new MatchingPayload(employeeAvailabilities, employeeConnectionStrengths); 
        BaseMessage message = new BaseMessage("GENERATE_PAIRS", payload);
        message.send(topic); 
        new CallbackResult(message.getConfiguration().getMessageId(), Matches.class).save(); 
    }
}
