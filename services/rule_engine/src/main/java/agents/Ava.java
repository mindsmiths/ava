package agents;

import java.util.*;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.templates.*;
import com.mindsmiths.armory.components.*;
import com.mindsmiths.ruleEngine.model.Agent;

import lombok.*;

import signals.DayChoiceSignal;


@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent { 

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void sendData(ArrayList<String> notFreeDays) {
        send("CultureMaster", new DayChoiceSignal(notFreeDays));
    }

    public void showScreen(BaseTemplate screen) {
        ArmoryAPI.showScreen(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, Map<String, BaseTemplate> screens) {
        ArmoryAPI.showScreens(getConnection("armory"), firstScreenId, screens);
    }

    public void bestTimeScreen() {
        List<CloudSelectComponent.Option> options = List.of(
            new CloudSelectComponent.Option("Mon", "1", true),
            new CloudSelectComponent.Option("Tue", "2", true),
            new CloudSelectComponent.Option("Wed", "3", true),
            new CloudSelectComponent.Option("Thu", "4", true),
            new CloudSelectComponent.Option("Fri", "5", true)
        );
        BaseTemplate daysScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent("When works best?"))
            .addComponent("text", new DescriptionComponent("Tick off the boxes of days when you are not free."))
            .addComponent("cloudSelect", new CloudSelectComponent("availableDays", options))
            .addComponent("submit", new PrimarySubmitButtonComponent("submit", "confirmDays"));
        showScreen(daysScreen);
    }

    public void confirmationScreen1() {
        BaseTemplate thanksScreen1 = new TemplateGenerator()
            .addComponent("title", new TitleComponent("Okay great, I'll send you an email when I catch up with others :)"))
            .addComponent("submit", new PrimarySubmitButtonComponent("Okay, thanks :)", "confirmThanks"));
        showScreen(thanksScreen1);
    }

    public void confirmationScreen2() {
        BaseTemplate thanksScreen2 = new TemplateGenerator()
            .addComponent("title", new TitleComponent("You're welcome. Stay tuned for the lunch :)"));
        showScreen(thanksScreen2);
    }

}
