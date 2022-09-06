package com.mindsmiths.pairingalgorithm;

import java.io.Serializable;
import java.util.ArrayList;

import com.mindsmiths.sdk.core.api.BaseMessage;
import com.mindsmiths.sdk.core.api.CallbackResult;
import com.mindsmiths.sdk.messaging.Messaging;




public class PairingAlgorithmAPI {
    private static final String topic = Messaging.getInputTopicName("pairing_algorithm");

    // ovaj cijeli dio se radi u rule engineu
    //pairData is a list of pairs<id, array of  size 5 where 0 means that person is unavailable on given day(mon-fri)>
    public static void generatePairs(ArrayList<AgentAvailableDays> pairData) { //primi stvari iz rule enginea (koji je u javi)
        Serializable payload = new DoSomethingPayload(pairData);  //napravi payload objekt 
        BaseMessage message = new BaseMessage("GENERATE_PAIRS", payload); // iz hrpe stvari u payload izdvoji korisne
        message.send(topic); // pokreni onaj pairing_algoritm.py, tj glavni dio
        new CallbackResult(message.getConfiguration().getMessageId(), Result.class).save(); //novi CallbackResult u koji ce nasadit izgenerirani rezultat
    }
}
