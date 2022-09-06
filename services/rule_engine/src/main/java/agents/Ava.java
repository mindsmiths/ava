package agents;
import java.util.ArrayList;

//import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.ruleEngine.model.Agent;
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

   /* public void redirect(String redirectUrl) {
        ArmoryAPI.redirect(getConnection("armory"), redirectUrl);
    }
    */

    /*public void showScreen() {
        //showScreen(new TitleTemplate(String.format("Thanks, %s!", name)));
    }*/

}
