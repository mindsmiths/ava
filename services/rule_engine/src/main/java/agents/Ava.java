package agents;
import java.util.ArrayList;
import java.util.List;

import com.mindsmiths.gpt3.GPT3AdapterAPI;
//import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.ruleEngine.util.Log;

import lombok.Data;
import lombok.ToString;
import signals.DayChoiceSignal;

@Data
@ToString

public class Ava extends Agent { 

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void sendData(ArrayList<Integer> freeDays) {
        send("PANIC", new DayChoiceSignal(freeDays));
    }

/*    public void askGPT3(String userMessage) {
        String intro = "This is generated idea of the person you are having lunch with.\n";
        simpleGPT3Request(intro + ""); //tu će valjda podaci o osobi koje dobijemo
    }

    public void simpleGPT3Request(String prompt) {
        Log.info("Prompt for GPT-3:\n" + prompt);
        GPT3AdapterAPI.complete(
            prompt, // input prompt
            "text-davinci-001", // model
            150, // max tokens
            0.9, // temperature
            1.0, // topP
            1, // N
            null, // logprobs
            false, // echo
            List.of("Zagreb", "Pilot"), // STOP words, promijeniti u početni i krajnji podatak?
            0.6, // presence penalty
            0.0, // frequency penalty
            1, // best of
            null // logit bias
        );
    }
   /* public void redirect(String redirectUrl) {
        ArmoryAPI.redirect(getConnection("armory"), redirectUrl);
    }
    */

    /*public void showScreen() {
        //showScreen(new TitleTemplate(String.format("Thanks, %s!", name)));
    }*/

}
