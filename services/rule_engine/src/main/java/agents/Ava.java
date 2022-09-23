package agents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.mindsmiths.armory.ArmoryAPI;
import com.mindsmiths.armory.components.ActionGroupComponent;
import com.mindsmiths.armory.components.BaseSubmitButtonComponent;
import com.mindsmiths.armory.components.CloudSelectComponent;
import com.mindsmiths.armory.components.DescriptionComponent;
import com.mindsmiths.armory.components.HeaderComponent;
import com.mindsmiths.armory.components.ImageComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TextAreaComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;
import com.mindsmiths.emailAdapter.AttachmentData;
import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.ruleEngine.util.Log;
import com.mindsmiths.sdk.utils.templating.Templating;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.AvaLunchCycleStage;
import models.EmployeeProfile;
import models.MonthlyCoreStage;
import models.OnboardingStage;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import models.AvaLunchCycleStage; 
import lombok.Data;

import models.EmployeeProfile;
import models.LunchReminderStage;
import models.OnboardingStage;
import models.MonthlyCoreStage;
import utils.Settings;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Ava extends Agent {
    private List<Days> availableDays = new ArrayList<>();
    private String match;
    private Days matchDay;
    private AvaLunchCycleStage lunchCycleStage;
    private OnboardingStage onboardingStage;
    private Map<String, EmployeeProfile> otherEmployees; 
    private Map<String, String> personalAnswers = new HashMap<String, String>();
    private boolean workingHours;
    private Date statsEmailLastSentAt;
    private Map<String, String> personalGuess = new HashMap<String, String>();
    private Integer correct;
    private MonthlyCoreStage monthlyCoreStage;
    private Date matchedWithEmailSentAt;
    private List<String> allQuestions = new ArrayList<>();
    private int silosCount;
    private String silosRisk;
    private LunchReminderStage lunchReminderStage;
    private List<String> lunchDeclineReasons = new ArrayList<>(); // track days
    private boolean manualTrigger;
    public final Integer LUNCH_QUIZ_QUESTIONS_COUNT = 3;
    public final Integer LUNCH_QUIZ_OPTIONS_COUNT = 3;

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
                .addComponent("submit", new PrimarySubmitButtonComponent("Submit", "confirmDays"));
        showScreen(daysScreen);
    }

    public void confirmingDaysScreen() {
        Option buttonOption = Mitems.getOptions("weekly-core.confirmation-of-choosen-available-days.button")[0];

        Map<String, BaseTemplate> screens = Map.of(
            "confirmDaysScreen", 
            new TemplateGenerator("confirmScreen")
                .setTemplateName("CenteredContentTemplate")
                .addComponent(
                    "title", 
                    new TitleComponent(
                        Mitems.getHTML("weekly-core.confirmation-of-choosen-available-days.title")
                    )
                )
                .addComponent(
                    "button",
                    new PrimarySubmitButtonComponent(buttonOption.getText(), buttonOption.getId())
                ),
            "confirmDaysAndThanksScreen",
            new TemplateGenerator("confirmAndThanksScreen")
                .setTemplateName("CenteredContentTemplate")
                .addComponent(
                    "title",
                    new TitleComponent(
                        Mitems.getText("weekly-core.stay-tuned-second-confirmation-of-available-days.title")
                    )
                )
        );
        showScreens("confirmDaysScreen", screens);
        Log.info(match);
        Log.info(this.match);
    }

    public void showFamiliarityQuizScreens() {
        Log.info(match);
        Log.info(this.match);
        Log.info("prvi personal answersi");
        Log.info(this.personalAnswers);
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String avaImagePath = Mitems.getText("onboarding.ava-image-path.path");
        List<Map<String, String>> names = getAllEmployeeNames();

        // Adding intro screen
        String introButton = Mitems.getText("onboarding.familiarity-quiz-intro.action");
        String introScreenTitle = Mitems.getText("onboarding.familiarity-quiz-intro.title");
        String introScreenDescription = Mitems.getHTML("onboarding.familiarity-quiz-intro.description");

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
                        .addComponent("header", new HeaderComponent(null, true))
                        .addComponent("question", new TitleComponent(questionText))
                        .addComponent(answersTag, new CloudSelectComponent(answersTag, names.get(questionNum - 1)))
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
                        .getHTML("onboarding.familiarity-quiz-goodbye.text");
                String finishFamiliarityQuizTitle = Mitems
                        .getText("onboarding.familiarity-quiz-goodbye.title");
                screens.put("finishfamiliarityquiz", new TemplateGenerator("finishfamiliarityquiz")
                        .addComponent("header", new HeaderComponent(null, true))
                        .addComponent("image", new ImageComponent(avaImagePath))
                        .addComponent("title", new TitleComponent(finishFamiliarityQuizTitle))
                        .addComponent("description", new DescriptionComponent(finishFamiliarityQuizText))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", familiarityQuizFinalButton,
                                "finished-familiarity-quiz")));
                break;
            }
        }
        showScreens("introScreen", screens);
    }

    public void showPersonalQuizScreens() {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String avaImagePath = Mitems.getText("onboarding.ava-image-path.path");
        // Adding intro screen
        String introButton = Mitems.getText("onboarding.personal-quiz-intro.action");
        String introScreenTitle = Mitems.getText("onboarding.personal-quiz-intro.title");
        String introScreenDescription = Mitems.getText("onboarding.personal-quiz-intro.description");

        screens.put("introScreen", new TemplateGenerator()
                .addComponent("image", new ImageComponent(avaImagePath))
                .addComponent("title", new TitleComponent(introScreenTitle))
                .addComponent("description", new DescriptionComponent(introScreenDescription))
                .addComponent("submit", new PrimarySubmitButtonComponent(introButton, "question1")));
        // Adding questions and final screens
        int questionNum = 1;
        while (true) {
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);
            try {
                screens.put(questionTag, new TemplateGenerator(questionTag)
                        .addComponent("header", new HeaderComponent(null, true))
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
                String finishPersonalQuiz = Mitems.getHTML("onboarding.finish-personal-quiz.text");

                screens.put("finishpersonalquiz", new TemplateGenerator("finishpersonalquiz")
                        .addComponent("image", new ImageComponent(avaImagePath))
                        .addComponent("title", new TitleComponent(finishPersonalQuiz))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", finishQuizButton[0].getText(), "finished-personal-quiz")));
                String goodbyeScreen = Mitems.getText("onboarding.finish-personal-quiz.goodbye-screen");
                screens.put("finished-personal-quiz", new TemplateGenerator("goodbye")
                        .setTemplateName("CenteredContentTemplate")
                        .addComponent("title", new TitleComponent(goodbyeScreen)));
                break;
            }
        }
        showScreens("introScreen", screens);
    }

    public void showFinalScreen() {
        String goodbyeScreen = Mitems.getText("onboarding.finish-personal-quiz.goodbye-screen");
        BaseTemplate screen = new TemplateGenerator("goodbye")
                                        .setTemplateName("CenteredContentTemplate")
                                        .addComponent("title", new TitleComponent(goodbyeScreen));
        showScreen(screen);
    }
    
    public void guessingQuizScreen() {
        Set<String> guessingQuestions = new LinkedHashSet<>();
        List<EmployeeProfile> otherEmployees = new ArrayList<>(this.otherEmployees.values());
        Random random = new Random();
        List<String> allQuestions = new ArrayList<String>(this.otherEmployees.get(this.match).getPersonalAnswers().keySet());

        while (guessingQuestions.size() <= Math.min(LUNCH_QUIZ_QUESTIONS_COUNT, allQuestions.size()))
            guessingQuestions.add(allQuestions.get(random.nextInt(allQuestions.size())));

        String title = Mitems.getText("weekly-core.guessing-quiz-intro-screen-title.title");
        title = Templating.recursiveRender(title, Map.of(
                "firstName", this.otherEmployees.get(this.match).getFirstName()
        ));

        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        screens.put("introGuessingScreen", new TemplateGenerator("introScreen")
            .addComponent("title", new TitleComponent(title))
            .addComponent("button", new PrimarySubmitButtonComponent(Mitems.getText("weekly-core.guessing-quiz-intro-screen-title.button"), "guessingQuestion1"))
        );

        int index = 0;
        for (String questionId : guessingQuestions) { 
            index++;
            Set<PrimarySubmitButtonComponent> options = new LinkedHashSet<>();
            options.add(new PrimarySubmitButtonComponent(
                this.otherEmployees.get(this.match).getPersonalAnswers().get(questionId),
                this.otherEmployees.get(this.match).getPersonalAnswers().get(questionId),
                String.format("guessingQuestion%d", index + 1)
            ));
            while (options.size() < Math.min(LUNCH_QUIZ_OPTIONS_COUNT, otherEmployees.size())) {
                int randomNumber = random.nextInt(this.otherEmployees.size());
                options.add(new PrimarySubmitButtonComponent(
                    otherEmployees.get(randomNumber).getPersonalAnswers().get(questionId),
                    otherEmployees.get(randomNumber).getPersonalAnswers().get(questionId),
                    String.format("guessingQuestion%d", index + 1)
                ));
            }
            List<BaseSubmitButtonComponent> answers = new ArrayList<BaseSubmitButtonComponent>(options);
            Collections.shuffle(answers);

            String question = Mitems.getText("weekly-core.guessing-quiz-" + questionId + ".question");
            question = Templating.recursiveRender(question, Map.of(
                "firstName", this.otherEmployees.get(this.match).getFirstName()
            ));

            screens.put(
                String.format("guessingQuestion%d", index),
                new TemplateGenerator(String.format("GuessingQuestion%d", index))
                    .addComponent("title", new TitleComponent(question))
                    .addComponent(String.format("actionGroup%d", index), new ActionGroupComponent(answers))
            );
        }

        screens.put(
            String.format("guessingQuestion%d", index + 1),
            new TemplateGenerator("confirmScreen")
                .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.confirming-quiz-guesses.title")))
                .addComponent("submit", new PrimarySubmitButtonComponent(Mitems.getText("weekly-core.confirming-quiz-guesses.button"), "confirmGuess"))
        );

        showScreens("introGuessingScreen", screens);
    }

    public void correctness() {
        for (Map.Entry<String, String> entry : personalGuess.entrySet()) {
            if (entry.getValue().equals(((otherEmployees.get(this.match)).getPersonalAnswers()).get(entry.getKey()))) {
                correct++;
            }
        }
    }   

    private List<Map<String, String>> getAllEmployeeNames() {
        List<Map<String, String>> names = new ArrayList<>();
        List<Integer> employeesPerQuestionDistribution = employeesPerQuestionDistribution();
        List<EmployeeProfile> employees = new ArrayList<>(otherEmployees.values());

        int startIndex = 0;
        int endIndex = 0;
        for (int len : employeesPerQuestionDistribution) {
            endIndex += len;
            Map<String, String> namesPerQuestion = new HashMap<>();

            for (EmployeeProfile employee : employees.subList(startIndex, endIndex)) {
                namesPerQuestion.put(employee.getFullName(), employee.getId());
            }
            names.add(namesPerQuestion);
            startIndex = endIndex;
        }
        return names;
    }

    public void sendWelcomeEmail(EmployeeProfile employee) throws IOException {
        String subject = Mitems.getText("onboarding.welcome-email.subject");
        String description = Mitems.getText("onboarding.welcome-email.description");
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("emailTemplates/EmailTemplate.html")).readAllBytes());
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

    public boolean allEmployeesFinishedOnboarding() {
        return otherEmployees.values().stream().allMatch(e -> (e.getOnboardingStage() == OnboardingStage.STATS_EMAIL)
                || (e.getOnboardingStage() == OnboardingStage.FINISHED));
    }

    public void sendMonthlyCoreEmail(EmployeeProfile employee) throws IOException {
        String subject = Mitems.getText("monthly-core.welcome-email.subject");
        String description = Mitems.getText("monthly-core.welcome-email.description");
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("emailTemplates/EmailTemplate.html")).readAllBytes());

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "description", description,
                "callToAction", Mitems.getText("monthly-core.welcome-email.action"),
                "firstName", employee.getFirstName(),
                "armoryUrl",
                String.format("%s/%s?trigger=start-monthly-Core", Settings.ARMORY_SITE_URL, getConnection("armory"))));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }

    public void showMonthlyQuizScreens() {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String avaImagePath = Mitems.getText("monthly-core.ava-image-path.path");
        List<Map<String, String>> names = getAllEmployeeNames();

        // Adding intro screen
        String introButton = Mitems.getText("monthly-core.familiarity-quiz-intro.action");
        String introScreenTitle = Mitems.getText("monthly-core.familiarity-quiz-intro.title");
        String introScreenDescription = Mitems.getText("monthly-core.familiarity-quiz-intro.description");

        screens.put("introScreen", new TemplateGenerator()
                .addComponent("image", new ImageComponent(avaImagePath))
                .addComponent("title", new TitleComponent(introScreenTitle))
                .addComponent("description", new DescriptionComponent(introScreenDescription))
                .addComponent("submit", new PrimarySubmitButtonComponent(introButton, "question1")));
        // Adding questions and final screen in familiarity quiz
        int questionNum = 1;
        String submitButton = Mitems.getText("monthly-core.familiarity-quiz-questions.action");

        while (true) {
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);

            try {
                String questionText = Mitems.getText("monthly-core.familiarity-quiz-questions." + questionTag);
                screens.put(questionTag, new TemplateGenerator(questionTag)
                        .addComponent("header", new HeaderComponent(null, questionNum > 1))
                        .addComponent("question", new TitleComponent(questionText))
                        .addComponent(answersTag, new CloudSelectComponent(answersTag, names.get(questionNum - 1)))
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
                buttonComponent.setValue("finishmonthlyquiz");

                String finishFamiliarityQuizText = Mitems
                        .getText("monthly-core.familiarity-quiz-goodbye.text");
                screens.put("finishmonthlyquiz", new TemplateGenerator("finishmonthlyquiz")

                        .addComponent("title", new TitleComponent(finishFamiliarityQuizText)));
                break;
            }
        }

        showScreens("introScreen", screens);
    }

    public void sendStatisticsEmail(EmployeeProfile employee) throws IOException {
        String subject = Mitems.getText("statistics.statistics-email.subject");
        String description = Mitems.getText("statistics.statistics-email.description");
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("emailTemplates/EmailTemplate.html")).readAllBytes());

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
        String employeeScreenButton = Mitems.getText("statistics.employee-number-screen.button");
        String employeeNumberScreenDescription = Mitems
                .getText("statistics.employee-number-screen.description");
        String employeeNumberScreenNumber = String.format("%d", otherEmployees.values().size() + 1);

        screens.put("employeeNumberScreen", new TemplateGenerator()
                .addComponent("title", new DescriptionComponent(employeeNumberScreenDescription))
                .addComponent("description", new TitleComponent(employeeNumberScreenNumber))
                .addComponent("submit", new PrimarySubmitButtonComponent(employeeScreenButton, "silosNumberScreen")));
        String silosScreenButton = Mitems.getText("statistics.silos-number-screen.button");
        String silosNumberScreenDescription = Mitems
                .getText("statistics.silos-number-screen.description");
        String silosNumberScreenNumber = String.format("%d", silosCount);

        screens.put("silosNumberScreen", new TemplateGenerator()
                .addComponent("header", new HeaderComponent(null, true))
                .addComponent("title", new DescriptionComponent(silosNumberScreenDescription))
                .addComponent("description", new TitleComponent(silosNumberScreenNumber))
                .addComponent("submit", new PrimarySubmitButtonComponent(silosScreenButton, "riskScreen")));
        String riskScreenButton = Mitems.getText("statistics.risk-screen.button");
        String riskScreenDescription = Mitems.getText("statistics.risk-screen.description");
        String riskScreenTitle = "moderate";

        screens.put("riskScreen", new TemplateGenerator()
                .addComponent("header", new HeaderComponent(null, true))
                .addComponent("title", new DescriptionComponent(riskScreenDescription))
                .addComponent("description", new TitleComponent(riskScreenTitle))
                .addComponent("submit", new PrimarySubmitButtonComponent(riskScreenButton, "finalScreen")));

        String finalScreenTitle = Mitems.getHTML("statistics.final-screen.title");
        screens.put("finalScreen", new TemplateGenerator()
                .setTemplateName("CenteredContentTemplate")
                .addComponent("description", new TitleComponent(finalScreenTitle)));

        showScreens("employeeNumberScreen", screens);
    }

    private List<Integer> employeesPerQuestionDistribution() {
        List<Integer> employeesPerQuestionDistribution = new ArrayList<Integer>();
        int numOfOtherEmployees = otherEmployees.size();
        int numOfQuestions = (int) Math.ceil((double) numOfOtherEmployees / 10.0);

        // Calculating number of employees per question
        double employeesPerQuestion;
        int employeesPerQuestionRounded;

        while (numOfOtherEmployees > 0) {
            employeesPerQuestion = (double) numOfOtherEmployees / (double) numOfQuestions;

            if (employeesPerQuestion % 1 != 0) {
                employeesPerQuestionRounded = (int) Math.ceil(employeesPerQuestion);
            } else {
                employeesPerQuestionRounded = (int) Math.floor(employeesPerQuestion);
            }

            employeesPerQuestionDistribution.add(employeesPerQuestionRounded);
            numOfOtherEmployees = numOfOtherEmployees - employeesPerQuestionRounded;
            numOfQuestions -= 1;
        }
        return employeesPerQuestionDistribution;
    }

    public void sendWeeklyEmail(EmployeeProfile employee) throws IOException {
        String subject = Mitems.getText("weekly-core.weekly-email.subject");
        String description = Mitems.getText("weekly-core.weekly-email.description");

        if (this.lunchReminderStage == LunchReminderStage.SECOND_EMAIL_SENT) { // second mail text
            subject = Mitems.getText("weekly-core.first-reminder-email.subject");
            description = Mitems.getText("weekly-core.first-reminder-email.description");

        } else if (this.lunchReminderStage == LunchReminderStage.THIRD_EMAIL_SENT) { // third mail text
            subject = Mitems.getText("weekly-core.second-reminder-email.subject");
            description = Mitems.getText("weekly-core.second-reminder-email.description");
        }

        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("emailTemplates/WeeklyEmailTemplate.html"))
                .readAllBytes());

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "text", description,
                "firstName", employee.getFirstName(),
                "button1", Mitems.getText("weekly-core.weekly-email.button1"),
                "button2", Mitems.getText("weekly-core.weekly-email.button2"),
                "armoryUrl1",
                String.format("%s/%s?trigger=start-weekly-core", Settings.ARMORY_SITE_URL, getConnection("armory")),
                "armoryUrl2", String.format("%s/%s?trigger=start-lunch-decline-reason-screen", Settings.ARMORY_SITE_URL,
                        getConnection("armory"))));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }

    public void showLunchInviteExpiredScreen() {
        BaseTemplate lunchInviteExpiredScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.message-about-not-working-hours-for-links.title")));
        showScreen(lunchInviteExpiredScreen);
    }
    
    public void showLunchDeclineReasonScreens() {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String lunchDeclineScreen = Mitems.getText("weekly-core.lunch-decline-reason.title");
        screens.put("LunchDecline", new TemplateGenerator()
                .addComponent("title", new TitleComponent(lunchDeclineScreen))
                .addComponent("answer", new TextAreaComponent("answer", true))
                .addComponent("submit", new PrimarySubmitButtonComponent("Submit", "finished-lunch-decline-form")));
        String finalScreenTitle = Mitems.getText("weekly-core.lunch-decline-reason.finalscreentitle");
        screens.put("finished-lunch-decline-form", new TemplateGenerator()
                .addComponent("description", new TitleComponent(finalScreenTitle)));
        showScreens("LunchDecline", screens);
    }

    public void showUserAlreadyRespondedScreen() {
        String goodbyeScreen = Mitems.getText("weekly-core.user-already-responded-screen.title");
        BaseTemplate screen = new TemplateGenerator("goodbye").addComponent("title", new TitleComponent(goodbyeScreen));
        showScreen(screen);
    }

    public void sendCalendarInvite(Days days, EmployeeProfile currentEmployee, EmployeeProfile otherEmployee)
            throws IOException {
        if (currentEmployee == null || otherEmployee == null)
            throw new RuntimeException("Ava.sendCalendarInvite called with null arguments!");

        String subject = Templating.recursiveRender(Mitems.getText("weekly-core.matching-mail.subject"), Map.of(
                "employeeName", otherEmployee.getFirstName(),
                "day", daysToPrettyString(days)));

        SendEmailPayload payload = new SendEmailPayload();
        payload.setRecipients(List.of(currentEmployee.getEmail()));
        payload.setSubject(subject);
        payload.setHtmlText(renderMatchmakingEmail(days, currentEmployee, otherEmployee)); // here goes HTML
        payload.setAttachments(
                List.of(new AttachmentData(getICSInvite(days, currentEmployee, otherEmployee), "invite.ics")));
        EmailAdapterAPI.newEmail(payload);
    }

    public String renderMatchmakingEmail(Days days, EmployeeProfile currentEmployee, EmployeeProfile otherEmployee)
            throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("emailTemplates/EmailTemplateCalendar.html"))
                .readAllBytes());
        return Templating.recursiveRender(htmlTemplate, Map.of(
            "title", Mitems.getText("weekly-core.matching-mail.title"),
            "description", Mitems.getHTML("weekly-core.matching-mail.description"),
            "otherName", otherEmployee.getFirstName(),
            "fullName", otherEmployee.getFullName(),
            "myName", currentEmployee.getFirstName(),
            "lunchDay", daysToPrettyString(days)
        ));
    }

    private byte[] getICSInvite(Days day, Employee currentEmployee, Employee otherEmployee) {
        try {
            Calendar invite = new Calendar();
            invite.getProperties().add(new ProdId("Ava"));
            invite.getProperties().add(Version.VERSION_2_0);
            invite.getProperties().add(CalScale.GREGORIAN);
            invite.getProperties().add(Method.REQUEST);

            int chosenDay = Map.of(
                    Days.MON, java.util.Calendar.MONDAY,
                    Days.TUE, java.util.Calendar.TUESDAY,
                    Days.WED, java.util.Calendar.WEDNESDAY,
                    Days.THU, java.util.Calendar.THURSDAY,
                    Days.FRI, java.util.Calendar.FRIDAY).get(day);

            java.util.Calendar now = java.util.Calendar.getInstance();
            java.util.Calendar saturday = nextDayOfWeek(now, java.util.Calendar.SATURDAY);

            java.util.Calendar lunchCalendarDate = nextDayOfWeek(saturday, chosenDay);
            lunchCalendarDate.set(java.util.Calendar.HOUR_OF_DAY, 12);
            lunchCalendarDate.set(java.util.Calendar.MINUTE, 0);
            lunchCalendarDate.set(java.util.Calendar.SECOND, 0);

            java.util.Calendar lunchCalendarDatePlusHour = (java.util.Calendar) lunchCalendarDate.clone();
            lunchCalendarDatePlusHour.set(java.util.Calendar.HOUR_OF_DAY, 13);

            String calendarEventName = Templating.recursiveRender(
                    Mitems.getText("weekly-core.matching-mail.calendar-event"),
                    Map.of(
                            "firstName", currentEmployee.getFirstName(),
                            "secondName", otherEmployee.getFirstName()));
            VEvent ev = new VEvent(new DateTime(lunchCalendarDate.getTime()),
                    new DateTime(lunchCalendarDatePlusHour.getTime()),
                    calendarEventName);
            ev.getProperties()
                    .add(new net.fortuna.ical4j.model.property.Attendee("mailto:" + currentEmployee.getEmail()));
            ev.getProperties()
                    .add(new net.fortuna.ical4j.model.property.Attendee("mailto:" + otherEmployee.getEmail()));

            invite.getComponents().add(ev);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            CalendarOutputter out = new CalendarOutputter();
            out.output(invite, byteOut);
            return byteOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String daysToPrettyString(Days days) {
        for (Option option : Mitems.getOptions("weekly-core.days.each-day")) {
            if (days.toString().equals(option.getId())) {
                return option.getText();
            }
        }
        return "Unknown";
    }

    public static java.util.Calendar nextDayOfWeek(java.util.Calendar now, int dow) {
        int diff = dow - now.get(java.util.Calendar.DAY_OF_WEEK);
        if (diff <= 0) {
            diff += 7;
        }
        now.add(java.util.Calendar.DAY_OF_MONTH, diff);
        return now;
    }
}
