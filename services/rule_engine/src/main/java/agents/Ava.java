package agents;

import com.mindsmiths.mitems.Mitems;

import java.util.*;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.templates.*;
import com.mindsmiths.armory.components.*;
import com.mindsmiths.armory.components.CloudSelectComponent.Option;
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
        String mon = Mitems.getText("weekly-core.days.monday");
        String tue = Mitems.getText("weekly-core.days.tuesday");
        String wed = Mitems.getText("weekly-core.days.wednsday");
        String thu = Mitems.getText("weekly-core.days.thursday");
        String fri = Mitems.getText("weekly-core.days.friday");

        List<CloudSelectComponent.Option> options = List.of(
            new CloudSelectComponent.Option(mon, "1", true),
            new CloudSelectComponent.Option(tue, "2", true),
            new CloudSelectComponent.Option(wed, "3", true),
            new CloudSelectComponent.Option(thu, "4", true),
            new CloudSelectComponent.Option(fri, "5", true)
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
