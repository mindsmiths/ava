package models;

import com.mindsmiths.armory.Screen;
import com.mindsmiths.armory.component.*;
import com.mindsmiths.emailAdapter.NewEmail;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.sdk.utils.Utils;
import com.mindsmiths.sdk.utils.templating.Templating;
import utils.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OnboardingTemplates {

    public static NewEmail welcomeEmail(Employee employee, String armoryConnectionId, String emailConnectionId)
            throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(
                        OnboardingTemplates.class.getClassLoader().getResourceAsStream(
                                "emailTemplates/EmailTemplate.html"))
                .readAllBytes());
        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", Mitems.getText("onboarding.welcome-email.description"),
                "callToAction", Mitems.getText("onboarding.welcome-email.action"),
                "firstName", employee.getFirstName(),
                "armoryUrl",
                String.format("%s/%s?trigger=start-onboarding", Settings.ARMORY_SITE_URL, armoryConnectionId),
                "now", Utils.datetimeToStr(Utils.now())));

        NewEmail email = new NewEmail();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(Mitems.getText("onboarding.welcome-email.subject"));
        email.setHtmlText(htmlBody);
        return email;
    }

    public static List<Screen> familiarityQuizScreens(Map<String, String> otherEmployeeNames) {
        List<Screen> screens = new ArrayList<>(List.of(new Screen("introScreen")
                        .add(new Title(Mitems.getText("onboarding.familiarity-quiz-intro.title")))
                        .add(new Image(Mitems.getText("onboarding.silos-image-path.connected")))
                        .add(new Description(Mitems.getHTML("onboarding.familiarity-quiz-intro.description")))
                        .add(new SubmitButton("intro-button", Mitems.getText("onboarding.familiarity-quiz-intro.action"), "secondIntroScreen"))
//                        .add(new Description("1/2")),
                ,
                new Screen("secondIntroScreen")
                        .add(new Header(null, true))
                        .add(new Title(Mitems.getText("onboarding.familiarity-quiz-second-intro.title")))
                        .add(new Image(Mitems.getText("onboarding.silos-image-path.devided")))
                        .add(new Description(Mitems.getText("onboarding.familiarity-quiz-second-intro.description")))
                        .add(new SubmitButton("second-intro-button", Mitems.getText("onboarding.familiarity-quiz-second-intro.action"), "question1"))
//                        .add(new Description("2/2")))
        )
        );

        Option[] questions = Mitems.getOptions("onboarding.familiarity-quiz-questions.questions");

        for (int i = 0; i < questions.length; i++) {
            String nextScreen = i == questions.length - 1 ? "finish-familiarity-quiz" : questions[i + 1].getId();
            screens.add(new Screen(questions[i].getId())
                            .add(new Header(null, true))
                            .add(new Title(questions[i].getText()))
                            .add(new Description(Mitems.getText("onboarding.familiarity-quiz-questions.question-description")))
                            .add(new CloudSelect("answers" + (i + 1),
                                    otherEmployeeNames.entrySet().stream().map(entry -> new CloudSelect.Option(entry.getValue(), entry.getKey())).toList()))
                            .add(new SubmitButton("submit", Mitems.getText("onboarding.familiarity-quiz-questions.action"), nextScreen))
//                          .add(new Description((i + 1) + "/" + questions.length))
            );
        }

        screens.add(new Screen("finish-familiarity-quiz")
                .add(new Header(null, true))
                .add(new Image(Mitems.getText("onboarding.ava-image-path.path")))
                .add(new Title(Mitems.getText("onboarding.familiarity-quiz-goodbye.title")))
                .add(new Description(Mitems.getText("onboarding.familiarity-quiz-goodbye.text")))
                .add(new SubmitButton("finish-familiarity-quiz", Mitems.getText("onboarding.familiarity-quiz-goodbye.action"), "final-screen")));
        screens.add(new Screen("final-screen")
                .setTemplate("CenteredContent")
                .add(new Title(Mitems.getText("onboarding.familiarity-quiz-goodbye.finish-screen"))));
        return screens;
    }

    public static Screen finalScreen() {
        return new Screen("goodbye")
                .setTemplate("CenteredContent")
                .add(new Title(Mitems.getText("onboarding.familiarity-quiz-goodbye.finish-screen")));
    }
}