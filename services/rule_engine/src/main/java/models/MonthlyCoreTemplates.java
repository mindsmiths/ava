package models;

import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.sdk.utils.templating.Templating;
import com.mindsmiths.armory.components.*;
import com.mindsmiths.armory.templates.*;
import utils.Settings;
import java.util.*;
import java.io.IOException;

public class MonthlyCoreTemplates {

    public SendEmailPayload monthlyCoreEmail(EmployeeProfile employee, String armoryConnectionId,
            String emailConnectionId) throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(
                        "emailTemplates/EmailTemplate.html"))
                .readAllBytes());
        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", Mitems.getText("monthly-core.welcome-email.description"),
                "callToAction", Mitems.getText("monthly-core.welcome-email.action"),
                "firstName", employee.getFirstName(),
                "armoryUrl",
                String.format("%s/%s?trigger=start-monthly-core", Settings.ARMORY_SITE_URL, armoryConnectionId)));

        SendEmailPayload email = new SendEmailPayload();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(Mitems.getText("monthly-core.welcome-email.subject"));
        email.setHtmlText(htmlBody);
        return email;
    }

    public static Map<String, BaseTemplate> monthlyQuizScreens(Map<String, String> otherEmployeeNames) {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        screens.put("introScreen", new TemplateGenerator()
                .addComponent("image",
                        new ImageComponent(Mitems.getText("monthly-core.ava-image-path.path")))
                .addComponent("title",
                        new TitleComponent(Mitems.getText("monthly-core.familiarity-quiz-intro.title")))
                .addComponent("description",
                        new DescriptionComponent(Mitems.getText("monthly-core.familiarity-quiz-intro.description")))
                .addComponent("submit",
                        new PrimarySubmitButtonComponent(
                                Mitems.getText("monthly-core.familiarity-quiz-intro.action"), "question1")));
        int questionNum = 1;
        while (true) {
            String questionTag = "question" + questionNum;
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);
            try {
                screens.put(questionTag, new TemplateGenerator(questionTag)
                        .addComponent("header", new HeaderComponent(null, questionNum > 1))
                        .addComponent("question", new TitleComponent(
                                Mitems.getText("monthly-core.familiarity-quiz-questions." + questionTag)))
                        .addComponent(answersTag, new CloudSelectComponent(answersTag, otherEmployeeNames))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", Mitems.getText("monthly-core.familiarity-quiz-questions.action"),
                                nextQuestionTag)));
                questionNum += 1;
            } catch (Exception e) {
                String wrongQuestionTag = "question" + String.valueOf(questionNum - 1);
                TemplateGenerator templateGenerator = (TemplateGenerator) screens.get(wrongQuestionTag);
                PrimarySubmitButtonComponent buttonComponent = (PrimarySubmitButtonComponent) templateGenerator
                        .getComponents()
                        .get("submit");
                buttonComponent.setValue("finish-monthly-quiz");
                buttonComponent.setInputId("finish-monthly-quiz");

                screens.put("finish-monthly-quiz", new TemplateGenerator("finish-monthly-quiz")
                        .addComponent("title", new TitleComponent(
                                Mitems.getText("monthly-core.familiarity-quiz-goodbye.text"))));
                break;
            }
        }
        return screens;
    }

    public BaseTemplate finalScreen() {
        BaseTemplate screen = new TemplateGenerator("goodbye")
                .setTemplateName("CenteredContentTemplate")
                .addComponent("title", new TitleComponent(
                        Mitems.getText("monthly-core.familiarity-quiz-goodbye.text")));
        return screen;
    }

}