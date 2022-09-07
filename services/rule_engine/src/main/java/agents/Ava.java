package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Data;
import lombok.ToString;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;

import java.util.List;
import java.util.Map;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.components.ActionGroupComponent;
import com.mindsmiths.armory.components.DescriptionComponent;
import com.mindsmiths.armory.components.InputComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;

import models.OnboardingStage;

@Data
@ToString(callSuper = true)
public class Ava extends Agent {
    public static String ID = "AVA";
    OnboardingStage onboardingStage = OnboardingStage.NotStarted;

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

    public void showFamiliarityQuizScreens() {
        Map<String, BaseTemplate> screens = Map.of(
                "question1", new TemplateGenerator("question1")
                        .addComponent("question", new TitleComponent("Who of the following do you know best?"))
                        .addComponent("actionGroup", new ActionGroupComponent(List.of(
                            new PrimarySubmitButtonComponent("Option1","Option 1", "question2"),
                            new PrimarySubmitButtonComponent("Option2","Option 2", "question2"),
                            new PrimarySubmitButtonComponent("Option3","Option 3", "question2")))),
                "question2", new TemplateGenerator("question2")
                        .addComponent("question", new TitleComponent("Who of the following do you know best?"))
                        .addComponent("actionGroup", new ActionGroupComponent(List.of(
                            new PrimarySubmitButtonComponent("Option 1", "Option 1", "finishFamiliarityQuiz"),
                            new PrimarySubmitButtonComponent("Option 2", "Option 2", "finishFamiliarityQuiz"),
                            new PrimarySubmitButtonComponent("Option 3", "Option 3", "finishFamiliarityQuiz")))),
                "finishFamiliarityQuiz", new TemplateGenerator("finishFamiliarityQuiz")
                            .addComponent("title", new TitleComponent("Thanks! I’ll check in with you from time to time to ask about other people too, but for now I have enough to get us started. Ready for fun part?"))
                            .addComponent("button", new PrimarySubmitButtonComponent("button", "Sure thing", "finished"))  
        );
        showScreens("question1", screens);
    }

    public void showPersonalQuizScreens() {
        Map<String, BaseTemplate> screens = Map.of(
            "introScreen", new TemplateGenerator("introScreen")
            .addComponent("title", new TitleComponent("Okay, first part done! Now, the fun part!I prepared 5 questions for you and your colleagues. Ready to start?"))
            .addComponent("button", new PrimarySubmitButtonComponent("button", "Let's do it", "question1")),
                "question1", new TemplateGenerator("question1")
                        .addComponent("question", new TitleComponent("What is the name of the last song you listened to?"))
                        .addComponent("answer", new InputComponent("answer", "Type your answer here", true))
                        .addComponent("submit", new PrimarySubmitButtonComponent("submit", "Submit", "question2")),
                "question2", new TemplateGenerator("question2")
                        .addComponent("question", new TitleComponent("What is the name of the last TV show you watched?"))
                        .addComponent("answer", new InputComponent("answer", "Type your answer here", true))
                        .addComponent("actionGroup", new PrimarySubmitButtonComponent("Option1","Option 1", "finishPersonalQuiz")),                       
                "finishPersonalQuiz", new TemplateGenerator("finishPersonalQuiz")
                            .addComponent("title", new TitleComponent("You are officially onboarded! Soon I’ll send you an email with connectivity data. Stay tuned!"))
                            .addComponent("button", new PrimarySubmitButtonComponent("button", "Great, can't wait!", "goodbye")),
                "goodbye", new TemplateGenerator("goodbye")
                            .addComponent("title", new TitleComponent("You are the best :*"))
        );
        showScreens("question1", screens);
    }
}