package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.List;

import com.mindsmiths.sdk.core.api.BaseMessage;
import com.mindsmiths.sdk.core.api.CallbackResult;
import com.mindsmiths.sdk.messaging.Messaging;


public class PairingAlgorithmAPI {
    private static final String topic = Messaging.getInputTopicName("pairing_algorithm");

    public static void generatePairs(List<AvaAvailability> avaAvailabilities) {
        Serializable payload = new AvaAvailabilitiesPayload(avaAvailabilities); 
        BaseMessage message = new BaseMessage("GENERATE_PAIRS", payload);
        message.send(topic); 
        new CallbackResult(message.getConfiguration().getMessageId(), Matches.class).save(); 
    }
}
