package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.List;

import com.mindsmiths.sdk.core.api.BaseMessage;
import com.mindsmiths.sdk.core.api.CallbackResult;
import com.mindsmiths.sdk.messaging.Messaging;


public class PairingAlgorithmAPI {
    private static final String topic = Messaging.getInputTopicName("pairing_algorithm");

    // ovaj cijeli dio se radi u rule engineu
    public static void generatePairs(List<AvaAvailability> avaAvailabilities) { //primi stvari iz rule enginea (koji je u javi)
        Serializable payload = new AvaAvailabilitiesPayload(avaAvailabilities);  //napravi payload objekt 
        BaseMessage message = new BaseMessage("GENERATE_PAIRS", payload); // iz hrpe stvari u payload izdvoji korisne
        message.send(topic); // pokreni onaj pairing_algoritm.py, tj glavni dio
        new CallbackResult(message.getConfiguration().getMessageId(), AllMatches.class).save(); //novi CallbackResult u koji ce nasadit izgenerirani rezultat
    }
}
