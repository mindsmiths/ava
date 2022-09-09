package agents;

import java.util.*;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.templates.*;
import com.mindsmiths.armory.components.*;
import com.mindsmiths.mitems.Mitems;
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

    public void sendDayChoice(ArrayList<String> notFreeDays) {
        send("CultureMaster", new DayChoiceSignal(notFreeDays));
    }

    public void showScreen(BaseTemplate screen) {
        ArmoryAPI.showScreen(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, Map<String, BaseTemplate> screens) {
        ArmoryAPI.showScreens(getConnection("armory"), firstScreenId, screens);
    }

    public void chooseAvailableDaysScreen() {
        String title = Mitems.getText("weekly-core.title-asking-for-available-days.title");
        String description = Mitems.getText("weekly-core.description-asking-for-available-days.text");
        com.mindsmiths.mitems.Option[] days = Mitems.getOptions("weekly-core.days.each-day");
        
        List<CloudSelectComponent.Option> options = List.of(
            new CloudSelectComponent.Option(days[0].getText(), "0", true),
            new CloudSelectComponent.Option(days[1].getText(), "1", true),
            new CloudSelectComponent.Option(days[2].getText(), "2", true),
            new CloudSelectComponent.Option(days[3].getText(), "3", true),
            new CloudSelectComponent.Option(days[4].getText(), "4", true)
        );

        BaseTemplate daysScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent(title))
            .addComponent("text", new DescriptionComponent(description))
            .addComponent("cloudSelect", new CloudSelectComponent("availableDays", options))
            .addComponent("submit", new PrimarySubmitButtonComponent("submit", "confirmDays"));
        showScreen(daysScreen);
    }

    public void confirmingDaysScreen() {
        String title1 = Mitems.getText("weekly-core.confirmation-of-choosen-available-days.title");
        String title2 = Mitems.getText("weekly-core.stay-tuned-second-confirmation-of-available-days.title");
        Map<String, BaseTemplate> screens = Map.of(
            "confirmDaysScreen", new TemplateGenerator("confirmScreen")
                .addComponent("title", new TitleComponent(title1))
                .addComponent("button", new PrimarySubmitButtonComponent("submit", "confirmDaysAndThanksScreen")),
            "confirmDaysAndThanksScreen", new TemplateGenerator("confirmAndThanksScreen")
                .addComponent("title", new TitleComponent(title2))
        );
    showScreens("confirmDaysScreen", screens);
    }

}
