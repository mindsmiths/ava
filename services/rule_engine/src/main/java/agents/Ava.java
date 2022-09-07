package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Data;
import lombok.ToString;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;

import java.util.Map;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.components.DescriptionComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;


@Data
@ToString(callSuper = true)
public class Ava extends Agent {
    public static String ID = "AVA";
    Boolean introScreenShowed = false;

    public Ava() {
        super();
        this.id = Ava.ID;
    }

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void showScreen(BaseTemplate screen) {
        ArmoryAPI.showScreen(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, Map<String, BaseTemplate> screens) {
        ArmoryAPI.showScreens(getConnection("armory"), firstScreenId, screens);
    }
    
    public void showIntroScreen() {
        String title = Mitems.getText("onboarding.intro-screen.title");
        String description = Mitems.getText("onboarding.intro-screen.description");
        Option[] button = Mitems.getOptions("onboarding.intro-screen.button");

        BaseTemplate screen = new TemplateGenerator()
                .addComponent("title", new TitleComponent(title))
                .addComponent("description", new DescriptionComponent(description))
                .addComponent("submit", new PrimarySubmitButtonComponent(button[0].getText(), "true"));
        showScreen(screen);
     }
}