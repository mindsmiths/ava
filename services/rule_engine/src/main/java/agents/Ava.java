package agents;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.ruleEngine.model.Agent;

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
        String avaImagePath = Mitems.getText("onboarding.familiarity-quiz.ava-image-path");
        
        // Adding intro screen
        Option[] introButton = Mitems.getOptions("onboarding.familiarity-quiz.intro-button");
        String introScreenTitle = Mitems.getText("onboarding.familiarity-quiz.intro-screen-title");
        String introScreenDescription = Mitems.getText("onboarding.familiarity-quiz.intro-screen-description");
        
        screens.put("introScreen", new TemplateGenerator()
            .addComponent("image", new ImageComponent(avaImagePath))
            .addComponent("title", new TitleComponent(introScreenTitle))
            .addComponent("description", new DescriptionComponent(introScreenDescription))
            .addComponent("submit", new PrimarySubmitButtonComponent(introButton[0].getText(), "question1"))
                   );
        // Adding questions and final screen in familiarity quiz
        int questionNum = 1;
        Option[] submitButton = Mitems.getOptions("onboarding.familiarity-quiz.submit-button");
        
        while(true){
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);

            try{
                String questionText = Mitems.getText("onboarding.familiarity-quiz."+ questionTag);
                screens.put(questionTag, new TemplateGenerator(questionTag)
                    .addComponent("question", new TitleComponent(questionText))
                    .addComponent(answersTag, new CloudSelectComponent(answersTag, Map.of(
                        "Tomislav Matić", "tomislav matić", "Emil Prpić", "emil pripić",
                        "Juraj Malenica", "juraj malenica", "Domagoj Blažanin", "domagoj blažanin")))
                    .addComponent(submitButton[0].getId(), new PrimarySubmitButtonComponent(
                        submitButton[0].getId(), submitButton[0].getText(), nextQuestionTag))
                            );
                questionNum += 1;
            }
            catch(Exception e){
                // Changing  button value 
                String wrongQuestionTag = "question" + String.valueOf(questionNum-1);
                TemplateGenerator templateGenerator = (TemplateGenerator) screens.get(wrongQuestionTag);
                PrimarySubmitButtonComponent buttonComponent = (PrimarySubmitButtonComponent) templateGenerator.getComponents()
                    .get(submitButton[0].getId());
                buttonComponent.setValue("finishfamiliarityquiz");
                
                Option[] familiarityQuizFinalButton = Mitems.getOptions("onboarding.familiarity-quiz.familiarity-quiz-final-button");
                String finishFamiliarityQuizText = Mitems.getText("onboarding.familiarity-quiz.finish-familiarity-quiz-text");
                screens.put("finishfamiliarityquiz", new TemplateGenerator("finishfamiliarityquiz")
                    .addComponent("image", new ImageComponent(avaImagePath))
                    .addComponent("title", new TitleComponent(finishFamiliarityQuizText))
                    .addComponent(familiarityQuizFinalButton[0].getId(), new PrimarySubmitButtonComponent(
                        familiarityQuizFinalButton[0].getId(), familiarityQuizFinalButton[0].getText(), "finished"))
                            );
                break;
            }
        }  
        showScreens("introScreen", screens);
    }

    public void showPersonalQuizScreens(){
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String avaImagePath = Mitems.getText("onboarding.personal-quiz.ava-image-path");

        // Adding intro screen
        Option[] acceptButton = Mitems.getOptions("onboarding.personal-quiz.accept-button");
        String introScreen = Mitems.getText("onboarding.personal-quiz.intro-screen");

        screens.put("introScreen", new TemplateGenerator("introScreen")
            .addComponent("title", new TitleComponent(introScreen))
            .addComponent(acceptButton[0].getId(), new PrimarySubmitButtonComponent(
                acceptButton[0].getId(), acceptButton[0].getText(), "question1"))
                    );
        // Adding questions and final screens
        int questionNum = 1;
        while(true){
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);
            Option[] submitButton = Mitems.getOptions("onboarding.personal-quiz.submit-button");

            try{
                String text = Mitems.getText("onboarding.personal-quiz."+ questionTag);
                screens.put(questionTag, new TemplateGenerator(questionTag)
                    .addComponent("question", new TitleComponent(text))
                    .addComponent(answersTag, new TextAreaComponent(answersTag, "Type your answer here", true))
                    .addComponent(submitButton[0].getId(), new PrimarySubmitButtonComponent(
                        submitButton[0].getId(), submitButton[0].getText(), nextQuestionTag))
                            );
                questionNum += 1;
            }
            catch(Exception e){
                // Changing  button value 
                String wrongQuestionTag = "question" + String.valueOf(questionNum-1);
                TemplateGenerator templateGenerator = (TemplateGenerator) screens.get(wrongQuestionTag);
                PrimarySubmitButtonComponent buttonComponent = (PrimarySubmitButtonComponent) templateGenerator.getComponents()
                    .get(submitButton[0].getId());
                buttonComponent.setValue("finishpersonalquiz");

                Option[] finishQuizButton = Mitems.getOptions("onboarding.personal-quiz.finish-quiz-button");
                String finishPersonalQuiz = Mitems.getText("onboarding.personal-quiz.finish-personal-quiz");
                
                screens.put("finishpersonalquiz", new TemplateGenerator("finishpersonalquiz")
                    .addComponent("image", new ImageComponent(avaImagePath))
                    .addComponent("title", new TitleComponent(finishPersonalQuiz))
                    .addComponent(finishQuizButton[0].getId(), new PrimarySubmitButtonComponent(
                        finishQuizButton[0].getId(), finishQuizButton[0].getText(), "goodbye"))
                            );
                String goodbyeScreen = Mitems.getText("onboarding.personal-quiz.goodbye-screen");
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
