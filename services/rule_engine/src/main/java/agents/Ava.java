package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.components.ActionGroupComponent;
import com.mindsmiths.armory.components.DescriptionComponent;
import com.mindsmiths.armory.components.InputComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;

import models.OnboardingStage;
import signals.DayChoiceSignal;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Ava extends Agent {
    OnboardingStage onboardingStage = OnboardingStage.NotStarted;

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
        String question1 = Mitems.getText("onboarding.familiarity-quiz.question1");
        String question2 = Mitems.getText("onboarding.familiarity-quiz.question2");
        String finishfamiliarityquiz = Mitems.getText("onboarding.familiarity-quiz.finishfamiliarityquiz");
        Option[] nextbutton = Mitems.getOptions("onboarding.familiarity-quiz.nextbutton");
        Map<String, BaseTemplate> screens = Map.of(
                "question1", new TemplateGenerator("question1")
                        .addComponent("question", new TitleComponent(question1))
                        .addComponent("actionGroup", new ActionGroupComponent(List.of(
                            new PrimarySubmitButtonComponent("Option1","Option 1", "question2"),
                            new PrimarySubmitButtonComponent("Option2","Option 2", "question2"),
                            new PrimarySubmitButtonComponent("Option3","Option 3", "question2")))),
                "question2", new TemplateGenerator("question2")
                        .addComponent("question", new TitleComponent(question2))
                        .addComponent("actionGroup", new ActionGroupComponent(List.of(
                            new PrimarySubmitButtonComponent("Option 1", "Option 1", "finishFamiliarityQuiz"),
                            new PrimarySubmitButtonComponent("Option 2", "Option 2", "finishFamiliarityQuiz"),
                            new PrimarySubmitButtonComponent("Option 3", "Option 3", "finishFamiliarityQuiz")))),
                "finishFamiliarityQuiz", new TemplateGenerator(finishfamiliarityquiz)
                            .addComponent("title", new TitleComponent("Thanks! I’ll check in with you from time to time to ask about other people too, but for now I have enough to get us started. Ready for fun part?"))
                            .addComponent("button", new PrimarySubmitButtonComponent("button", nextbutton[0].getText(), "finished"))  
        );
        showScreens("question1", screens);
    }

    public void showPersonalQuizScreens() {
        String finishpersonalquiz = Mitems.getText("onboarding.personal-quiz.finishpersonalquiz");
        String goodbye_screen = Mitems.getText("onboarding.personal-quiz.goodbye-screen");
        String intro_screen = Mitems.getText("onboarding.personal-quiz.intro-screen");
        String question1 = Mitems.getText("onboarding.personal-quiz.question1");
        String question2 = Mitems.getText("onboarding.personal-quiz.question2");

        Map<String, BaseTemplate> screens = Map.of(
            "introScreen", new TemplateGenerator("introScreen")
            .addComponent("title", new TitleComponent(intro_screen))
            .addComponent("button", new PrimarySubmitButtonComponent("button", "Let's do it", "question1")),
                "question1", new TemplateGenerator("question1")
                        .addComponent("question", new TitleComponent(question1))
                        .addComponent("answer", new InputComponent("answer", "Type your answer here", true))
                        .addComponent("submit", new PrimarySubmitButtonComponent("submit", "Submit", "question2")),
                "question2", new TemplateGenerator("question2")
                        .addComponent("question", new TitleComponent(question2))
                        .addComponent("answer", new InputComponent("answer", "Type your answer here", true))
                        .addComponent("button", new PrimarySubmitButtonComponent("Option1","Option 1", "finishPersonalQuiz")),                       
                "finishPersonalQuiz", new TemplateGenerator("finishPersonalQuiz")
                            .addComponent("title", new TitleComponent(finishpersonalquiz))
                            .addComponent("button", new PrimarySubmitButtonComponent("button", "Great, can't wait!", "goodbye")),
                "goodbye", new TemplateGenerator("goodbye")
                            .addComponent("title", new TitleComponent(goodbye_screen))
        );
        showScreens("question1", screens);
    }

    public void sendData(ArrayList<Integer> freeDays) {
        send("CultureMaster", new DayChoiceSignal(freeDays));
    }
 
}
