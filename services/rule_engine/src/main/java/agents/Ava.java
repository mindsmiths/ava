package agents;

import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import utils.Settings;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.components.*;
import com.mindsmiths.armory.templates.*;
import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.utils.templating.Templating;

import models.AvaLunchCycleStage;
import models.EmployeeProfile;
import models.OnboardingStage;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Ava extends Agent {
    private List<Days> availableDays = new ArrayList<>();
    private String match;
    private Days matchDay;
    private AvaLunchCycleStage lunchCycleStage = AvaLunchCycleStage.FIND_AVAILABILITY;
    private OnboardingStage onboardingStage;
    private Map<String, EmployeeProfile> otherEmployees;
    private boolean workingHours;
    private Date statsEmailLastSentAt;
    private int silosCount;
    private int silosRisk;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for (String day : availableDaysStr) {
            this.availableDays.add(Days.valueOf(day));
        }
    }

    public void showScreen(BaseTemplate screen) {
        ArmoryAPI.showScreen(getConnection("armory"), screen);
    }

    public void showScreens(String firstScreenId, Map<String, BaseTemplate> screens) {
        ArmoryAPI.showScreens(getConnection("armory"), firstScreenId, screens);
    }

    public void showLunchInviteExpiredScreen() {
        BaseTemplate lunchInviteExpiredScreen = new TemplateGenerator()
                .addComponent("title", new TitleComponent(
                        Mitems.getText("weekly-core.message-about-not-working-hours-for-links.title")));
        showScreen(lunchInviteExpiredScreen);
    }

    public void chooseAvailableDaysScreen() {
        Option[] days = Mitems.getOptions("weekly-core.days.each-day");
        List<CloudSelectComponent.Option> options = new ArrayList<>();

        for (Option option : days)
            options.add(new CloudSelectComponent.Option(option.getText(), option.getId(), true));

        BaseTemplate daysScreen = new TemplateGenerator()
                .addComponent("title",
                        new TitleComponent(Mitems.getText("weekly-core.title-asking-for-available-days.title")))
                .addComponent("text",
                        new DescriptionComponent(
                                Mitems.getText("weekly-core.description-asking-for-available-days.text")))
                .addComponent("cloudSelect", new CloudSelectComponent("availableDays", options))
                .addComponent("submit", new PrimarySubmitButtonComponent("submit", "confirmDays"));
        showScreen(daysScreen);
    }

    public void showNotAvailableScreen() {
        BaseTemplate notAvailableScreen = new TemplateGenerator()
                .addComponent("title", new TitleComponent(
                        Mitems.getText("weekly-core.title-for-person-who-is-not-available-any-day.title")));
        // implement free form where they have to explain why they are not available
        showScreen(notAvailableScreen);
    }

    public void confirmingDaysScreen() {
        Map<String, BaseTemplate> screens = Map.of(
                "confirmDaysScreen", new TemplateGenerator("confirmScreen")
                        .addComponent("title",
                                new TitleComponent(
                                        Mitems.getText("weekly-core.confirmation-of-choosen-available-days.title")))
                        .addComponent("button",
                                new PrimarySubmitButtonComponent("submit", "confirmDaysAndThanksScreen")),
                "confirmDaysAndThanksScreen", new TemplateGenerator("confirmAndThanksScreen")
                        .addComponent("title", new TitleComponent(
                                Mitems.getText("weekly-core.stay-tuned-second-confirmation-of-available-days.title"))));
        showScreens("confirmDaysScreen", screens);
    }

    public void showFamiliarityQuizScreens() {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String avaImagePath = Mitems.getText("onboarding.ava-image-path.path");
        Map<String, String> names = getAllEmployeeNames();

        // Adding intro screen
        String introButton = Mitems.getText("onboarding.familiarity-quiz-intro.action");
        String introScreenTitle = Mitems.getText("onboarding.familiarity-quiz-intro.title");
        String introScreenDescription = Mitems.getText("onboarding.familiarity-quiz-intro.description");

        screens.put("introScreen", new TemplateGenerator()
                .addComponent("image", new ImageComponent(avaImagePath))
                .addComponent("title", new TitleComponent(introScreenTitle))
                .addComponent("description", new DescriptionComponent(introScreenDescription))
                .addComponent("submit", new PrimarySubmitButtonComponent(introButton, "question1")));
        // Adding questions and final screen in familiarity quiz
        int questionNum = 1;
        String submitButton = Mitems.getText("onboarding.familiarity-quiz-questions.action");

        while (true) {
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);

            try {
                String questionText = Mitems.getText("onboarding.familiarity-quiz-questions." + questionTag);
                screens.put(questionTag, new TemplateGenerator(questionTag)
                        .addComponent("question", new TitleComponent(questionText))
                        .addComponent(answersTag, new CloudSelectComponent(answersTag, names))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", submitButton, nextQuestionTag)));
                questionNum += 1;

            } catch (Exception e) {
                // Changing button value
                String wrongQuestionTag = "question" + String.valueOf(questionNum - 1);

                TemplateGenerator templateGenerator = (TemplateGenerator) screens.get(wrongQuestionTag);
                PrimarySubmitButtonComponent buttonComponent = (PrimarySubmitButtonComponent) templateGenerator
                        .getComponents()
                        .get("submit");
                buttonComponent.setValue("finishfamiliarityquiz");

                String familiarityQuizFinalButton = Mitems
                        .getText("onboarding.familiarity-quiz-goodbye.action");
                String finishFamiliarityQuizText = Mitems
                        .getText("onboarding.familiarity-quiz-goodbye.text");
                screens.put("finishfamiliarityquiz", new TemplateGenerator("finishfamiliarityquiz")
                        .addComponent("image", new ImageComponent(avaImagePath))
                        .addComponent("title", new TitleComponent(finishFamiliarityQuizText))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", familiarityQuizFinalButton,
                                "finished-familiarity-quiz")));
                String goodbyeScreen = Mitems.getText("onboarding.familiarity-quiz-goodbye.goodbye-screen");
                screens.put("finished-familiarity-quiz", new TemplateGenerator("goodbye")
                        .addComponent("title", new TitleComponent(goodbyeScreen)));
                break;
            }
        }
        showScreens("introScreen", screens);
    }

    public void showPersonalQuizScreens() {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String avaImagePath = Mitems.getText("onboarding.ava-image-path.path");

        // Adding questions and final screens
        int questionNum = 1;
        while (true) {
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);
            try {
                screens.put(questionTag, new TemplateGenerator(questionTag)
                        .addComponent("question",
                                new TitleComponent(Mitems.getText(
                                        String.format("onboarding.personal-quiz-%s.%s", questionTag, questionTag))))
                        .addComponent(answersTag, new TextAreaComponent(answersTag, "Type your answer here", true))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit",
                                Mitems.getText(String.format("onboarding.personal-quiz-%s.action", questionTag)),
                                nextQuestionTag)));
                questionNum += 1;
            } catch (Exception e) {
                // Changing button value
                String wrongQuestionTag = "question" + String.valueOf(questionNum - 1);
                TemplateGenerator templateGenerator = (TemplateGenerator) screens.get(wrongQuestionTag);
                PrimarySubmitButtonComponent buttonComponent = (PrimarySubmitButtonComponent) templateGenerator
                        .getComponents()
                        .get("submit");
                buttonComponent.setValue("finishpersonalquiz");

                Option[] finishQuizButton = Mitems.getOptions("onboarding.finish-personal-quiz.button");
                String finishPersonalQuiz = Mitems.getText("onboarding.finish-personal-quiz.text");

                screens.put("finishpersonalquiz", new TemplateGenerator("finishpersonalquiz")
                        .addComponent("image", new ImageComponent(avaImagePath))
                        .addComponent("title", new TitleComponent(finishPersonalQuiz))
                        .addComponent(finishQuizButton[0].getId(), new PrimarySubmitButtonComponent(
                                finishQuizButton[0].getId(), finishQuizButton[0].getText(), "finished-personal-quiz")));
                break;
            }
        }
        showScreens("question1", screens);
    }

    public void showFinalScreen() {
        String goodbyeScreen = Mitems.getText("onboarding.familiarity-quiz.goodbye-screen");
        BaseTemplate screen = new TemplateGenerator("goodbye").addComponent("title", new TitleComponent(goodbyeScreen));
        showScreen(screen);
    }

    private String getFullName(EmployeeProfile employee) {
        return employee.getFirstName() + " " + employee.getLastName();
    }

    private Map<String, String> getAllEmployeeNames() {
        Map<String, String> names = new HashMap<>();
        for (EmployeeProfile employee : otherEmployees.values()) {
            names.put(getFullName(employee), employee.getId());
        }
        return names;
    }

    public void sendWelcomeEmail(EmployeeProfile employee) throws IOException {
        String subject = Mitems.getText("onboarding.welcome-email.subject");
        String description = Mitems.getText("onboarding.welcome-email.description");
        String htmlTemplate = String.join("",
                Files.readAllLines(Paths.get("EmailTemplate.html"), StandardCharsets.UTF_8));

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", description,
                "callToAction", Mitems.getText("onboarding.welcome-email.action"),
                "firstName", employee.getFirstName(),
                "armoryUrl",
                String.format("%s/%s?trigger=start-onboarding", Settings.ARMORY_SITE_URL, getConnection("armory"))));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }

    public void sendStatisticsEmail(EmployeeProfile employee) throws IOException {
        String subject = Mitems.getText("statistics.statistics-email.subject");
        String description = Mitems.getText("statistics.statistics-email.description");
        String htmlTemplate = String.join("",
                Files.readAllLines(Paths.get("EmailTemplate.html"), StandardCharsets.UTF_8));

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", description,
                "callToAction", Mitems.getText("statistics.statistics-email.action"),
                "firstName", employee.getFirstName(),
                "armoryUrl",
                String.format("%s/%s?trigger=show-stats", Settings.ARMORY_SITE_URL, getConnection("armory"))));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }

    public void showStatisticsScreens() {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        // String nextButton = ;
        String employeeNumberScreenDescription = Mitems
                .getText("statistics.stats-screens.employee-number-screen-description");
        String employeeNumberScreenNumber = String.format("%d", otherEmployees.values().size() + 1);

        screens.put("employeeNumberScreen", new TemplateGenerator()
                .addComponent("title", new DescriptionComponent(employeeNumberScreenDescription))
                .addComponent("description", new TitleComponent(employeeNumberScreenNumber))
                .addComponent("submit", new PrimarySubmitButtonComponent("Next", "silosNumberScreen")));

        String silosNumberScreenDescription = Mitems
                .getText("statistics.stats-screens.silos-number-screen-description");
        String silosNumberScreenNumber = String.format("%d", 3);

        screens.put("silosNumberScreen", new TemplateGenerator()
                .addComponent("title", new DescriptionComponent(silosNumberScreenDescription))
                .addComponent("description", new TitleComponent(silosNumberScreenNumber))
                .addComponent("submit", new PrimarySubmitButtonComponent("Next", "riskScreen")));

        String riskScreenDescription = Mitems.getText("statistics.stats-screens.risk-screen-description");
        String riskScreenTitle = "moderate";

        screens.put("riskScreen", new TemplateGenerator()
                .addComponent("title", new DescriptionComponent(riskScreenDescription))
                .addComponent("description", new TitleComponent(riskScreenTitle))
                .addComponent("submit", new PrimarySubmitButtonComponent("Close", "finalScreen")));

        String finalScreenTitle = Mitems.getText("statistics.stats-screens.final-screen-title");
        screens.put("finalScreen", new TemplateGenerator()
                .addComponent("description", new TitleComponent(finalScreenTitle)));

        showScreens("employeeNumberScreen", screens);
    }
}