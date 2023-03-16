package models;

import com.mindsmiths.armory.Screen;
import com.mindsmiths.armory.component.*;
import com.mindsmiths.emailAdapter.NewEmail;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.sdk.utils.templating.Templating;
import utils.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MonthlyCoreTemplates {

    public static NewEmail monthlyCoreEmail(Employee employee, String armoryConnectionId,
                                            String emailConnectionId) throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(
                        MonthlyCoreTemplates.class.getClassLoader().getResourceAsStream(
                                "emailTemplates/EmailTemplate.html"))
                .readAllBytes());
        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", Mitems.getText("monthly-core.welcome-email.description"),
                "callToAction", Mitems.getText("monthly-core.welcome-email.action"),
                "firstName", employee.getFirstName(),
                "armoryUrl",
                String.format("%s/%s?trigger=start-monthly-core", Settings.ARMORY_SITE_URL, armoryConnectionId)));

        NewEmail email = new NewEmail();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(Mitems.getText("monthly-core.welcome-email.subject"));
        email.setHtmlText(htmlBody);
        return email;
    }

    public static List<Screen> monthlyQuizScreens(Map<String, String> otherEmployeeNames) {
        Option[] questions = Mitems.getOptions("onboarding.familiarity-quiz-questions.questions");
        List<Screen> screens = new ArrayList<>(
                List.of(new Screen("introScreen")
                        .add(new Image(Mitems.getText("monthly-core.ava-image-path.path")))
                        .add(new Title(Mitems.getText("monthly-core.familiarity-quiz-intro.title")))
                        .add(new Description(Mitems.getText("monthly-core.familiarity-quiz-intro.description")))
                        .add(new SubmitButton("submit", Mitems.getText("monthly-core.familiarity-quiz-intro.action"), questions[0].getId()))));

        for (int i = 0; i < questions.length; i++) {
            String nextScreen = i == questions.length - 1 ? "finish-monthly-quiz" : questions[i + 1].getId();
            screens.add(new Screen(questions[i].getId())
                    .add(new Header(null, i > 0))
                    .add(new Title(questions[i].getText()))
                    .add(new Description(Mitems.getText("onboarding.familiarity-quiz-questions.question-description")))
                    .add(new CloudSelect("answers" + (i + 1), otherEmployeeNames))
                    .add(new SubmitButton("submit", Mitems.getText("onboarding.familiarity-quiz-questions.action"), nextScreen))
                    .add(new Description((i + 1) + "/" + questions.length)));
        }
        screens.add(new Screen("finish-monthly-quiz")
                .add(new Title(Mitems.getText("monthly-core.familiarity-quiz-goodbye.text"))));
        return screens;
    }

    public static Screen finalScreen() {
        return new Screen("goodbye")
                .setTemplate("CenteredContent")
                .add(new Title(Mitems.getText("monthly-core.familiarity-quiz-goodbye.text")));
    }

}