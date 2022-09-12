package agents;

import java.util.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.templates.*;
import com.mindsmiths.armory.components.*;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;

import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.pairingalgorithm.Days;


@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent {
    private List<Days> availableDays = new ArrayList<>();
    private String matchName;
    private Days matchDay;
    private AvaWeeklyStage weeklyStage = AvaWeeklyStage.FIND_AVAILABILITY;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for(String day: availableDaysStr) {
            this.availableDays.add(Days.valueOf(day));
        }
    }

    public void showScreen(BaseTemplate screen) {
        ArmoryAPI.showScreen(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, Map<String, BaseTemplate> screens) {
        ArmoryAPI.showScreens(getConnection("armory"), firstScreenId, screens);
    }

    public void notWorkingHours() {
        BaseTemplate notWorkingScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.message-about-not-working-hours-for-links.title")));
        showScreen(notWorkingScreen);
    }

    public void chooseAvailableDaysScreen() {
        Option[] days = Mitems.getOptions("weekly-core.days.each-day");
        List<CloudSelectComponent.Option> options = new ArrayList<>();
        
        for(Option option: days)
            options.add(new CloudSelectComponent.Option(option.getText(), option.getId(), true));

        BaseTemplate daysScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.title-asking-for-available-days.title")))
            .addComponent("text", new DescriptionComponent(Mitems.getText("weekly-core.description-asking-for-available-days.text")))
            .addComponent("cloudSelect", new CloudSelectComponent("availableDays", options))
            .addComponent("submit", new PrimarySubmitButtonComponent("submit", "confirmDays"));
        showScreen(daysScreen);
    }

    public void showNotAvailable() {
        BaseTemplate notAvailableScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.title-for-person-who-is-not-available-any-day.title")));
        // implement free form where they have to explain why they are not available
        showScreen(notAvailableScreen);
    }

    public void confirmingDaysScreen() {
        Map<String, BaseTemplate> screens = Map.of(
            "confirmDaysScreen", new TemplateGenerator("confirmScreen")
                .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.confirmation-of-choosen-available-days.title")))
                .addComponent("button", new PrimarySubmitButtonComponent("submit", "confirmDaysAndThanksScreen")),
            "confirmDaysAndThanksScreen", new TemplateGenerator("confirmAndThanksScreen")
                .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.stay-tuned-second-confirmation-of-available-days.title")))
        );
        showScreens("confirmDaysScreen", screens);
    }

}
