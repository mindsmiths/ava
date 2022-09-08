package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.components.DescriptionComponent;
import com.mindsmiths.armory.components.ImageComponent;
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
    OnboardingStage onboardingStage;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void showScreen(BaseTemplate screen) {
        ArmoryAPI.showScreen(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, Map<String, BaseTemplate> screens) {
        ArmoryAPI.showScreens(getConnection("armory"), firstScreenId, screens);
    }

    public void showFamiliarityQuizScreens(){
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        Option[] buttons = Mitems.getOptions("onboarding.familiarity-quiz.buttons");
        
        //Adding intro screen
        String introScreenTitle = Mitems.getText("onboarding.familiarity-quiz.introscreentitle");
        String introScreenDescription = Mitems.getText("onboarding.familiarity-quiz.introscreendescription");
        screens.put("introScreen", new TemplateGenerator()
            .addComponent("image", new ImageComponent("/public/Ava.png"))
            .addComponent("title", new TitleComponent(introScreenTitle))
            .addComponent("description", new DescriptionComponent(introScreenDescription))
            .addComponent("submit", new PrimarySubmitButtonComponent(buttons[0].getText(), "question1"))
                   );
        //Adding questions and final screen in familiarity quiz
        int questionNum = 1;
        while(true){
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);

            try{
                String questionText = Mitems.getText("onboarding.familiarity-quiz."+ questionTag);
                screens.put(questionTag, new TemplateGenerator(questionTag)
                    .addComponent("question", new TitleComponent(questionText))
                    .addComponent("answers", new CloudSelectComponent("answers", Map.of(
                        "Tomislav Matić", "tomislav matić", "Emil Prpić", "emil pripić",
                        "Juraj Malenica", "juraj malenica", "Domagoj Blažanin", "domagoj blažanin")))
                    .addComponent(buttons[1].getId(), new PrimarySubmitButtonComponent(
                        buttons[1].getId(), buttons[1].getText(), nextQuestionTag))
                            );
                questionNum += 1;
            }
            catch(Exception e){
                //String wrongQuestionTag = "question" + String.valueOf(questionNum);
                //screens.get(wrongQuestionTag).getComponents().get("submit");
                String finishFamiliarityQuiz = Mitems.getText("onboarding.familiarity-quiz.finishfamiliarityquiz");
                
                screens.put(questionTag, new TemplateGenerator("finishfamiliarityquiz")
                    .addComponent("image", new ImageComponent("/public/Ava.png"))
                    .addComponent("title", new TitleComponent(finishFamiliarityQuiz))
                    .addComponent(buttons[2].getId(), new PrimarySubmitButtonComponent(
                        buttons[2].getId(), buttons[2].getText(), "finished"))
                            );
                break;
            }
        }  
    showScreens("introScreen", screens);
    }

    public void showPersonalQuizScreens(){
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        Option[] buttons = Mitems.getOptions("onboarding.personal-quiz.buttons");

        //Adding intro screen
        String introScreen = Mitems.getText("onboarding.personal-quiz.introscreen");
        screens.put("introScreen", new TemplateGenerator("introScreen")
            .addComponent("title", new TitleComponent(introScreen))
            .addComponent(buttons[0].getId(), new PrimarySubmitButtonComponent(
                buttons[0].getId(), buttons[0].getText(), "question1"))
                    );
        //Adding questions and final screens
        int questionNum = 1;
        while(true){
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);

            try{
                String text = Mitems.getText("onboarding.personal-quiz."+ questionTag);
                screens.put(questionTag, new TemplateGenerator(questionTag)
                    .addComponent("question", new TitleComponent(text))
                    .addComponent("answer", new TextAreaComponent("answer", "Type your answer here", true))
                    .addComponent(buttons[1].getId(), new PrimarySubmitButtonComponent(
                        buttons[1].getId(), buttons[1].getText(), nextQuestionTag))
                            );
                questionNum += 1;
            }
            catch(Exception e){
                String finishFamiliarityQuiz = Mitems.getText("onboarding.familiarity-quiz.finishfamiliarityquiz");
                screens.put(questionTag, new TemplateGenerator("finishfamiliarityquiz")
                    .addComponent("image", new ImageComponent("/public/Ava.png"))
                    .addComponent("title", new TitleComponent(finishFamiliarityQuiz))
                    .addComponent(buttons[2].getId(), new PrimarySubmitButtonComponent(
                        buttons[2].getId(), buttons[2].getText(), "goodbye"))
                            );
                String goodbyeScreen = Mitems.getText("onboarding.personal-quiz.goodbyescreen");
                screens.put("goodbye", new TemplateGenerator("goodbye")
                    .addComponent("title", new TitleComponent(goodbyeScreen))
                            );
                break;
            }
        }
    showScreens("introScreen", screens);
    }

    public void sendData(ArrayList<Integer> freeDays) {
        send("CultureMaster", new DayChoiceSignal(freeDays));
    }
 
}
