package models;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mindsmiths.armory.components.CloudSelectComponent;
import com.mindsmiths.armory.components.DescriptionComponent;
import com.mindsmiths.armory.components.HeaderComponent;
import com.mindsmiths.armory.components.ImageComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.ruleEngine.util.Log;
import com.mindsmiths.sdk.utils.templating.Templating;

import utils.Settings;

public class OnboardingTemplates {

    public SendEmailPayload welcomeEmail(EmployeeProfile employee, String armoryConnectionId, String emailConnectionId)
            throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(
                        "emailTemplates/EmailTemplate.html"))
                .readAllBytes());
        Log.warn(htmlTemplate);
        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", Mitems.getText("onboarding.welcome-email.description"),
                "callToAction", Mitems.getText("onboarding.welcome-email.action"),
                "firstName", employee.getFirstName(),
                "armoryUrl",
                String.format("%s/%s?trigger=start-onboarding", Settings.ARMORY_SITE_URL, armoryConnectionId)));
        Log.warn(htmlBody);
        SendEmailPayload email = new SendEmailPayload();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(Mitems.getText("onboarding.welcome-email.subject"));
        email.setHtmlText(htmlBody);
        return email;
    }

    public Map<String, BaseTemplate> familiarityQuizScreens(Map<String, String> otherEmployeeNames) {
        Map<String, BaseTemplate> screens = new HashMap<>();
        screens.put("introScreen", new TemplateGenerator()
                .addComponent("title",
                        new TitleComponent(Mitems.getText("onboarding.familiarity-quiz-intro.title")))
                .addComponent("image",
                        new ImageComponent(Mitems.getText("onboarding.silos-image-path.connected")))
                .addComponent("description",
                        new DescriptionComponent(Mitems.getHTML("onboarding.familiarity-quiz-intro.description")))
                .addComponent("submit",
                        new PrimarySubmitButtonComponent(Mitems.getText("onboarding.familiarity-quiz-intro.action"),
                                "secondIntroScreen"))
                .addComponent("pageNum", new DescriptionComponent("1/2")));
        screens.put("secondIntroScreen", new TemplateGenerator()
                .addComponent("header", new HeaderComponent(null, true))
                .addComponent("title",
                        new TitleComponent(Mitems.getText("onboarding.familiarity-quiz-second-intro.title")))
                .addComponent("image",
                        new ImageComponent(Mitems.getText("onboarding.silos-image-path.devided")))
                .addComponent("description",
                        new DescriptionComponent(
                                Mitems.getText("onboarding.familiarity-quiz-second-intro.description")))
                .addComponent("submit",
                        new PrimarySubmitButtonComponent(
                                Mitems.getText("onboarding.familiarity-quiz-second-intro.action"), "question1"))
                .addComponent("pageNum", new DescriptionComponent("2/2")));
        int questionNum = 1;
        while (true) {
            String questionTag = "question" + questionNum;
            String nextQuestionTag = "question" + (questionNum + 1);
            String answersTag = "answers" + questionNum;
            try {
                String questionText = Mitems.getText("onboarding.familiarity-quiz-questions." + questionTag);
                screens.put(questionTag, new TemplateGenerator(questionTag)
                        .addComponent("header", new HeaderComponent(null, true))
                        .addComponent("question", new TitleComponent(questionText))
                        .addComponent("description", new DescriptionComponent(
                                Mitems.getText("onboarding.familiarity-quiz-questions.question-description")))
                        .addComponent(answersTag, new CloudSelectComponent(answersTag, otherEmployeeNames))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", Mitems.getText("onboarding.familiarity-quiz-questions.action"),
                                nextQuestionTag))
                        .addComponent("pageNum", new DescriptionComponent(questionNum + "/3")));
                questionNum += 1;
            } catch (Exception e) {
                String wrongQuestionTag = "question" + (questionNum - 1);
                TemplateGenerator templateGenerator = (TemplateGenerator) screens.get(wrongQuestionTag);
                PrimarySubmitButtonComponent buttonComponent = (PrimarySubmitButtonComponent) templateGenerator
                        .getComponents().get("submit");
                buttonComponent.setInputId("finish-familiarity-quiz");
                buttonComponent.setValue("finish-familiarity-quiz");

                screens.put("finish-familiarity-quiz", new TemplateGenerator("finish-familiarity-quiz")
                        .addComponent("header", new HeaderComponent(null, true))
                        .addComponent("image",
                                new ImageComponent(Mitems.getText("onboarding.ava-image-path.path")))
                        .addComponent("title",
                                new TitleComponent(Mitems.getText("onboarding.familiarity-quiz-outro.title")))
                        .addComponent("description",
                                new DescriptionComponent(Mitems.getHTML("onboarding.familiarity-quiz-outro.text")))
                        .addComponent("final-screen",
                                new PrimarySubmitButtonComponent(
                                        "final-familiarity-screen",
                                        Mitems.getText("onboarding.familiarity-quiz-outro.action"),
                                        "final-familiarity-screen")));
                break;
            }
        }
        return screens;
    }

    public BaseTemplate personalQuizIntroScreens() {
        BaseTemplate screen = new TemplateGenerator("introScreen")
                .addComponent("image", new ImageComponent(Mitems.getText("onboarding.ava-image-path.path")))
                .addComponent("title", new TitleComponent(Mitems.getText("onboarding.personal-quiz-intro.title")))
                .addComponent("description", new DescriptionComponent(
                        Mitems.getText("onboarding.personal-quiz-intro.description")))
                .addComponent("submit", new PrimarySubmitButtonComponent(
                        "start-personal-quiz",
                        Mitems.getText("onboarding.personal-quiz-intro.action"),
                        "start-personal-quiz"));
        return screen;
    }

    /*
     * public void showPersonalQuizScreens(String questionTag, int
     * numOfPersonalAnswers) {
     * int questionNum = Integer.valueOf(questionTag.replace("question", ""));
     * String answersTag = "answers" + String.valueOf(questionNum);
     * 
     * BaseTemplate screen = new TemplateGenerator(questionTag)
     * .addComponent("question", new TitleComponent(
     * Mitems.getText(String.format("onboarding.personal-quiz-%s.%s", questionTag,
     * questionTag))))
     * .addComponent(answersTag, new TextAreaComponent(answersTag,
     * "Type your answer here"))
     * .addComponent("actionGroup", new ActionGroupComponent(List.of(
     * new PrimarySubmitButtonComponent(
     * "submit",
     * Mitems.getText(String.format("onboarding.personal-quiz-%s.action",
     * questionTag)),
     * "submit"),
     * new PrimarySubmitButtonComponent(
     * "skip",
     * "Skip this question",
     * "skip"))))
     * .addComponent("pageNum", new
     * DescriptionComponent(String.valueOf(numOfPersonalAnswers + 1) + "/3"));
     * showScreen(screen);
     * }
     * 
     * public void showPersonalQuizOutroScreens() {
     * Map<String, BaseTemplate> screens = new HashMap<>();
     * Option[] finishQuizButton =
     * Mitems.getOptions("onboarding.finish-personal-quiz.button");
     * String finishPersonalQuiz =
     * Mitems.getHTML("onboarding.finish-personal-quiz.text");
     * String avaImagePath = Mitems.getText("monthly-core.ava-image-path.path");
     * 
     * screens.put("finishpersonalquiz", new TemplateGenerator("finishpersonalquiz")
     * .addComponent("image", new ImageComponent(avaImagePath))
     * .addComponent("title", new TitleComponent(finishPersonalQuiz))
     * .addComponent("submit", new PrimarySubmitButtonComponent(
     * "qoodbyescreen", finishQuizButton[0].getText(), "qoodbyescreen")));
     * 
     * String goodbyeScreen =
     * Mitems.getText("onboarding.finish-personal-quiz.goodbye-screen");
     * screens.put("qoodbyescreen", new TemplateGenerator("goodbye")
     * .setTemplateName("CenteredContentTemplate")
     * .addComponent("title", new TitleComponent(goodbyeScreen)));
     * 
     * showScreens("finishpersonalquiz", screens);
     * }
     */
    public BaseTemplate finalScreen() {
        BaseTemplate screen = new TemplateGenerator("goodbye")
                .setTemplateName("CenteredContentTemplate")
                .addComponent("title", new TitleComponent(
                        Mitems.getText("onboarding.familiarity-quiz-goodbye.finish-screen")));
        return screen;
    }
}