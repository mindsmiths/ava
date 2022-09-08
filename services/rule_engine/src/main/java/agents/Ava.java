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
import com.mindsmiths.armory.components.ImageComponent;
import com.mindsmiths.armory.components.InputComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TextAreaComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;
import com.mindsmiths.armory.components.CloudSelectComponent;

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
                .addComponent("image", new ImageComponent("/public/Ava.png"))
                .addComponent("title", new TitleComponent(title))
                .addComponent("description", new DescriptionComponent(description))
                .addComponent("submit", new PrimarySubmitButtonComponent(button[0].getText(), "true"));
        showScreen(screen);
     }

    public void showFamiliarityQuizScreens() {
        String question1 = Mitems.getText("onboarding.familiarity-quiz.question1");
        String question2 = Mitems.getText("onboarding.familiarity-quiz.question2");
        String question3 = Mitems.getText("onboarding.familiarity-quiz.question3");
        String question4 = Mitems.getText("onboarding.familiarity-quiz.question4");
        String finishfamiliarityquiz = Mitems.getText("onboarding.familiarity-quiz.finishfamiliarityquiz");
        Option[] buttons = Mitems.getOptions("onboarding.familiarity-quiz.buttons");

        Map<String, BaseTemplate> screens = Map.of(
                "question1", new TemplateGenerator("question1")
                        .addComponent("question", new TitleComponent(question1))
                        .addComponent("answers", new CloudSelectComponent("answers", Map.of(
                            "Tomislav Matić", "tomislav matić", "Emil Prpić", "emil pripić",
                            "Juraj Malenica", "juraj malenica", "Domagoj Blažanin", "domagoj blažanin")))
                        .addComponent(buttons[0].getId(), new PrimarySubmitButtonComponent(buttons[0].getId(),
                            buttons[0].getText(), "question2")),
                "question2", new TemplateGenerator("question2")
                        .addComponent("question", new TitleComponent(question2))
                        .addComponent("answers", new CloudSelectComponent("answers", Map.of(
                            "Tomislav Matić", "tomislav matić", "Emil Prpić", "emil pripić",
                            "Juraj Malenica", "juraj malenica", "Domagoj Blažanin", "domagoj blažanin")))
                        .addComponent(buttons[0].getId(), new PrimarySubmitButtonComponent(buttons[0].getId(),
                        buttons[0].getText(), "question3")),
                "question3", new TemplateGenerator("question3")
                        .addComponent("question", new TitleComponent(question3))
                        .addComponent("answers", new CloudSelectComponent("answers", Map.of(
                            "Tomislav Matić", "tomislav matić", "Emil Prpić", "emil pripić",
                            "Juraj Malenica", "juraj malenica", "Domagoj Blažanin", "domagoj blažanin")))
                        .addComponent(buttons[0].getId(), new PrimarySubmitButtonComponent(buttons[0].getId(),
                        buttons[0].getText(), "question4")),
                "question4", new TemplateGenerator("question4")
                        .addComponent("question", new TitleComponent(question4))
                        .addComponent("answers", new CloudSelectComponent("answers", Map.of(
                            "Tomislav Matić", "tomislav matić", "Emil Prpić", "emil pripić",
                            "Juraj Malenica", "juraj malenica", "Domagoj Blažanin", "domagoj blažanin")))
                        .addComponent(buttons[0].getId(), new PrimarySubmitButtonComponent(buttons[0].getId(),
                        buttons[0].getText(), "finishFamiliarityQuiz")),
                "finishFamiliarityQuiz", new TemplateGenerator("finishfamiliarityquiz")
                            .addComponent("image", new ImageComponent("/public/Ava.png"))
                            .addComponent("title", new TitleComponent(finishfamiliarityquiz))
                            .addComponent(buttons[1].getId(), new PrimarySubmitButtonComponent(buttons[1].getId(),
                            buttons[1].getText(), "finished"))
        );
        showScreens("question1", screens);
    }

    public void showPersonalQuizScreens() {
        String finishpersonalquiz = Mitems.getText("onboarding.personal-quiz.finishpersonalquiz");
        String goodbye_screen = Mitems.getText("onboarding.personal-quiz.goodbye-screen");
        String intro_screen = Mitems.getText("onboarding.personal-quiz.intro-screen");
        String question1 = Mitems.getText("onboarding.personal-quiz.question1");
        String question2 = Mitems.getText("onboarding.personal-quiz.question2");
        Option[] buttons = Mitems.getOptions("onboarding.personal-quiz.buttons");

        Map<String, BaseTemplate> screens = Map.of(
            "introScreen", new TemplateGenerator("introScreen")
                .addComponent("title", new TitleComponent(intro_screen))
                .addComponent(buttons[2].getId(), new PrimarySubmitButtonComponent(buttons[2].getId(), buttons[2].getText(), "question1")),
            "question1", new TemplateGenerator("question1")
                .addComponent("question", new TitleComponent(question1))
                .addComponent("answer", new TextAreaComponent("answer", "Type your answer here", true))
                .addComponent(buttons[0].getId(), new PrimarySubmitButtonComponent(buttons[0].getId(),
                    buttons[0].getText(), "question2")),
            "question2", new TemplateGenerator("question2")
                .addComponent("question", new TitleComponent(question2))
                .addComponent("answer", new TextAreaComponent("answer", "Type your answer here", true))
                .addComponent(buttons[0].getId(), new PrimarySubmitButtonComponent(buttons[0].getId(),
                    buttons[0].getText(), "finishPersonalQuiz")),                       
            "finishPersonalQuiz", new TemplateGenerator("finishPersonalQuiz")
                .addComponent("image", new ImageComponent("/public/Ava.png"))
                .addComponent("title", new TitleComponent(finishpersonalquiz))
                .addComponent(buttons[1].getId(), new PrimarySubmitButtonComponent(buttons[1].getId(),
                    buttons[0].getText(), "goodbye")),
            "goodbye", new TemplateGenerator("goodbye")
                .addComponent("title", new TitleComponent(goodbye_screen))
        );
        showScreens("introScreen", screens);
    }

    public void sendData(ArrayList<Integer> freeDays) {
        send("CultureMaster", new DayChoiceSignal(freeDays));
    }
 
}
