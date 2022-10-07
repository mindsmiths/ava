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
                                new TitleComponent(Mitems.getText("onboarding.familiarity-quiz-goodbye.title")))
                        .addComponent("description",
                                new DescriptionComponent(Mitems.getHTML("onboarding.familiarity-quiz-goodbye.text")))
                        .addComponent("final-screen",
                                new PrimarySubmitButtonComponent(
                                        "final-screen",
                                        Mitems.getText("onboarding.familiarity-quiz-goodbye.action"),
                                        "final-screen")));
                screens.put("final-screen", new TemplateGenerator("final-screen")
                        .addComponent("title", new TitleComponent(
                                Mitems.getText("onboarding.familiarity-quiz-goodbye.finish-screen"))));
                break;
            }
        }
        return screens;
    }

    public BaseTemplate finalScreen() {
        BaseTemplate screen = new TemplateGenerator("goodbye")
                .setTemplateName("CenteredContentTemplate")
                .addComponent("title", new TitleComponent(
                        Mitems.getText("onboarding.familiarity-quiz-goodbye.finish-screen")));
        return screen;
    }
}