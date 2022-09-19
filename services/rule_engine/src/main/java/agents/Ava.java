package agents;

import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import com.mindsmiths.ruleEngine.util.Log;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.utils.templating.Templating;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.emailAdapter.AttachmentData;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import models.AvaLunchCycleStage;
import models.EmployeeProfile;
import models.OnboardingStage;
import utils.Settings;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Ava extends Agent {
    private List<Days> availableDays = new ArrayList<>();
    private String match;
    private Days matchDay;
    private AvaLunchCycleStage lunchCycleStage = AvaLunchCycleStage.FIND_AVAILABILITY;
    private Employee employee;
    private OnboardingStage onboardingStage;
    private Map<String, EmployeeProfile> otherEmployees;
    private boolean workingHours;
    private Date statsEmailLastSentAt;
    private Date matchedWithEmailSentAt;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for(String day: availableDaysStr) {
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
            .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.message-about-not-working-hours-for-links.title")));
        showScreen(lunchInviteExpiredScreen);
    }

    public void chooseAvailableDaysScreen() {
        Option[] days = Mitems.getOptions("weekly-core.days.each-day");
        List<CloudSelectComponent.Option> options = new ArrayList<>();
        
        for(Option option: days)
            options.add(new CloudSelectComponent.Option(option.getText(), option.getId(), true));

        BaseTemplate daysScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.title-asking-for-available-days.title")))
            .addComponent("text", new DescriptionComponent(Mitems.getText("weekly-core.description-asking-for-available-days.text")))
            .addComponent("cloudSelect", new CloudSelectComponent("availableDays", options))
            .addComponent("submit", new PrimarySubmitButtonComponent("submit", "confirmDays"));
        showScreen(daysScreen);
    }

    public void showNotAvailableScreen() {
        BaseTemplate notAvailableScreen = new TemplateGenerator()
            .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.title-for-person-who-is-not-available-any-day.title")));
        // implement free form where they have to explain why they are not available
        showScreen(notAvailableScreen);
    }

    public void confirmingDaysScreen() {
        Map<String, BaseTemplate> screens = Map.of(
            "confirmDaysScreen", new TemplateGenerator("confirmScreen")
                .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.confirmation-of-choosen-available-days.title")))
                .addComponent("button", new PrimarySubmitButtonComponent("submit", "confirmDaysAndThanksScreen")),
            "confirmDaysAndThanksScreen", new TemplateGenerator("confirmAndThanksScreen")
                .addComponent("title", new TitleComponent(Mitems.getText("weekly-core.stay-tuned-second-confirmation-of-available-days.title")))
        );
        showScreens("confirmDaysScreen", screens);
    }

    public void showFamiliarityQuizScreens() {
        Map<String, BaseTemplate> screens = new HashMap<String, BaseTemplate>();
        String avaImagePath = Mitems.getText("onboarding.ava-image-path.path");
        List<Map<String, String>> names = getAllEmployeeNames();

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

        while(true) { 
            String questionTag = "question" + String.valueOf(questionNum);
            String nextQuestionTag = "question" + String.valueOf(questionNum + 1);
            String answersTag = "answers" + String.valueOf(questionNum);

            try {
                String questionText = Mitems.getText("onboarding.familiarity-quiz-questions." + questionTag);
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
                buttonComponent.setValue("finishfamiliarityquiz");

                String familiarityQuizFinalButton = Mitems
                    .getText("onboarding.familiarity-quiz-goodbye.action");
                String finishFamiliarityQuizText =  Mitems
                    .getText("onboarding.familiarity-quiz-goodbye.text");
                screens.put("finishfamiliarityquiz", new TemplateGenerator("finishfamiliarityquiz")
                        .addComponent("image", new ImageComponent(avaImagePath))
                        .addComponent("title", new TitleComponent(finishFamiliarityQuizText))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", familiarityQuizFinalButton,
                                "finished-familiarity-quiz")));
                String goodbyeScreen =  Mitems.getText("onboarding.familiarity-quiz-goodbye.goodbye-screen");
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
                        .addComponent("header", new HeaderComponent(null, questionNum > 1))
                        .addComponent("question", new TitleComponent(Mitems.getText(String.format("onboarding.personal-quiz-%s.%s", questionTag, questionTag))))
                        .addComponent(answersTag, new TextAreaComponent(answersTag, "Type your answer here", true))
                        .addComponent("submit", new PrimarySubmitButtonComponent(
                                "submit", Mitems.getText(String.format("onboarding.personal-quiz-%s.action", questionTag)), nextQuestionTag)));
                questionNum += 1;
            } catch (Exception e) {
                // Changing button value
                String wrongQuestionTag = "question" + String.valueOf(questionNum - 1);
                TemplateGenerator templateGenerator = (TemplateGenerator) screens.get(wrongQuestionTag);
                PrimarySubmitButtonComponent buttonComponent = (PrimarySubmitButtonComponent) templateGenerator
                        .getComponents()
                        .get("submit");
                buttonComponent.setValue("finishpersonalquiz");

                Option[] finishQuizButton =  Mitems.getOptions("onboarding.finish-personal-quiz.button");
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

    private List<Map<String, String>> getAllEmployeeNames() {
        List<Map<String, String>> names = new ArrayList<>();
        List<Integer> employeesPerQuestionDistribution = employeesPerQuestionDistribution();
        List<EmployeeProfile> employees = new ArrayList<>(otherEmployees.values());

        int startIndex = 0;
        int endIndex = 0;
        for(int len : employeesPerQuestionDistribution) {
            endIndex += len;
            Map<String, String> namesPerQuestion = new HashMap<>();

            for(EmployeeProfile employee : employees.subList(startIndex, endIndex)) {
                namesPerQuestion.put(employee.getFullName(), employee.getId());
            }
            names.add(namesPerQuestion);
            startIndex = endIndex;
        }
        return names;
    }

    public void sendPairingMail() throws IOException {
        String subject = Mitems.getText("onboarding.welcome-email.subject");
        String description = Mitems.getText("onboarding.welcome-email.description");
        String htmlTemplate = String.join("", Files.readAllLines(Paths.get("EmailTemplate.html"), StandardCharsets.UTF_8));

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
            "description", description,
            "callToAction", "Let's go",
            "armoryUrl", String.format("%s/%s?trigger=start-weekly-core", Settings.ARMORY_SITE_URL, getConnection("armory"))
        ));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e); 
    }

    public void sendWelcomeEmail(Employee employee) throws IOException {
        String subject = Mitems.getText("onboarding.welcome-email.subject");
        String description = Mitems.getText("onboarding.welcome-email.description");
        String htmlTemplate = String.join("", Files.readAllLines(Paths.get("EmailTemplate.html"), StandardCharsets.UTF_8));

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
            "description", description,
            "callToAction", Mitems.getText("onboarding.welcome-email.action"),
            "firstName", employee.getFirstName(),
            "armoryUrl", String.format("%s/%s?trigger=start-onboarding", Settings.ARMORY_SITE_URL, getConnection("armory"))
        ));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }

    public void sendStatisticsEmail(EmployeeProfile employee) throws IOException {
        String subject = Mitems.getText("statistics.statistics-email.subject");
        String description = Mitems.getText("statistics.statistics-email.description");
        String htmlTemplate = String.join("", Files.readAllLines(Paths.get("EmailTemplate.html"), StandardCharsets.UTF_8));

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
            "description", description,
            "callToAction", Mitems.getText("statistics.statistics-email.action"),
            "firstName", employee.getFirstName(),
            "armoryUrl", String.format("%s/%s?trigger=show-stats", Settings.ARMORY_SITE_URL, getConnection("armory"))
        ));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }

    private List<Integer> employeesPerQuestionDistribution() {
        List<Integer> employeesPerQuestionDistribution = new ArrayList<Integer>();
        int numOfOtherEmployees = otherEmployees.size();
        int numOfQuestions = (int) Math.ceil((double) numOfOtherEmployees / 10.0);
        
        // Calculating number of employees per question
        double employeesPerQuestion;
        int employeesPerQuestionRounded;

        while (numOfOtherEmployees > 0) {
            employeesPerQuestion = (double) numOfOtherEmployees/ (double) numOfQuestions;

            if (employeesPerQuestion % 1 != 0) {
                employeesPerQuestionRounded = (int) Math.ceil(employeesPerQuestion);
            }
            else {
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
        String htmlTemplate = String.join("", Files.readAllLines(Paths.get("EmailTemplate.html"), StandardCharsets.UTF_8));

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
            "description", description,
            "callToAction", Mitems.getText("weekly-core.weekly-email.button"),
            "firstName", employee.getFirstName(),
            "armoryUrl", String.format("%s/%s?trigger=start-weekly-core", Settings.ARMORY_SITE_URL, getConnection("armory"))
        ));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }

    public String dayHelperFunction(Days days) {
        switch (days.toString()) {
            case "MON":
                return "Monday";
            case "TUE":
                return "Tuesday";
            case "WED":
                return "Wednesday";
            case "THU":
                return "Thursday";
            case "FRI":
                return "Friday";
            default:
                return "unkown";
        }
    }

    public static java.util.Calendar nextDayOfWeek(java.util.Calendar now, int dow) {
        int diff = dow - now.get(java.util.Calendar.DAY_OF_WEEK);
        if (diff <= 0) {
            diff += 7;
        }
        now.add(java.util.Calendar.DAY_OF_MONTH, diff);
        return now;
    }
    
    public void sendCalendarInvite(Days days, EmployeeProfile currentEmployee, EmployeeProfile otherEmployee) throws IOException {
        if(currentEmployee == null || otherEmployee == null)
            throw new RuntimeException("Ava.sendCalendarInvite called with null arguments!");
        
        String subject = Templating.recursiveRender(Mitems.getText("weekly-core.matching-mail.subject"), Map.of(
                "employeeName", otherEmployee.getFirstName(),
                "day", dayHelperFunction(days)
        ));
    
        SendEmailPayload payload = new SendEmailPayload();
        payload.setRecipients(List.of(currentEmployee.getEmail()));
        payload.setSubject(subject);
        payload.setHtmlText(renderEmailBody(days, currentEmployee, otherEmployee)); // here goes HTML
        payload.setAttachments(List.of(new AttachmentData(getICSInvite(days, currentEmployee, otherEmployee), "invite.ics")));
        EmailAdapterAPI.newEmail(payload);
    }

    public String renderEmailBody(Days days, EmployeeProfile currentEmployee, EmployeeProfile otherEmployee) throws IOException {
        String template = String.join("", Files.readAllLines(Paths.get("EmailTemplateCalendar.html"), StandardCharsets.UTF_8));
        String description = Templating.recursiveRender(template, Map.of(
            "title", Mitems.getText("weekly-core.matching-mail.title"),
            "description", Mitems.getText("weekly-core.matching-mail.description"),
            "otherName", otherEmployee.getFirstName(),
            "fullName", otherEmployee.getFullName(),
            "myName", currentEmployee.getFirstName(),
            "lunchDay", dayHelperFunction(days)
        ));

        return description;
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
                Days.FRI, java.util.Calendar.FRIDAY
            ).get(day);

            java.util.Calendar now = java.util.Calendar.getInstance();
            java.util.Calendar saturday = nextDayOfWeek(now, java.util.Calendar.SATURDAY);

            java.util.Calendar lunchCalendarDate = nextDayOfWeek(saturday, chosenDay);
            lunchCalendarDate.set(java.util.Calendar.HOUR_OF_DAY, 12);
            lunchCalendarDate.set(java.util.Calendar.MINUTE, 0);
            lunchCalendarDate.set(java.util.Calendar.SECOND, 0);

            Log.info(lunchCalendarDate);

            // "Lunch with " + otherEmployee.getFirstName() + " on " + dayHelperFunction(day)

            java.util.Calendar lunchCalendarDatePlusHour = (java.util.Calendar) lunchCalendarDate.clone();
            lunchCalendarDatePlusHour.set(java.util.Calendar.HOUR_OF_DAY, 13);
            Log.info(lunchCalendarDatePlusHour);

            VEvent ev = new VEvent(new DateTime(lunchCalendarDate.getTime()),
                                   new DateTime(lunchCalendarDatePlusHour.getTime()),
                                   "Ava lunch: " + currentEmployee.getFirstName() + "-" + otherEmployee.getFirstName());
            ev.getProperties().add(new net.fortuna.ical4j.model.property.Attendee("mailto:" + currentEmployee.getEmail()));
            ev.getProperties().add(new net.fortuna.ical4j.model.property.Attendee("mailto:" + otherEmployee.getEmail())); 
            
            invite.getComponents().add(ev);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            CalendarOutputter out = new CalendarOutputter();
            out.output(invite, byteOut);
            return byteOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
