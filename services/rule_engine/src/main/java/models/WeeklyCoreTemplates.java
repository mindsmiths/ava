package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mindsmiths.armory.components.CloudSelectComponent;
import com.mindsmiths.armory.components.DescriptionComponent;
import com.mindsmiths.armory.components.PrimarySubmitButtonComponent;
import com.mindsmiths.armory.components.TextAreaComponent;
import com.mindsmiths.armory.components.TitleComponent;
import com.mindsmiths.armory.templates.BaseTemplate;
import com.mindsmiths.armory.templates.TemplateGenerator;
import com.mindsmiths.emailAdapter.AttachmentData;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.utils.templating.Templating;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import java.io.ByteArrayOutputStream;

import utils.Settings;

public class WeeklyCoreTemplates {

    public SendEmailPayload weeklyEmail(EmployeeProfile employee, LunchReminderStage lunchReminderStage,
            String armoryConnectionId, String emailConnectionId) throws IOException {
        String subject = Mitems.getText("weekly-core.weekly-email.subject");
        String description = Mitems.getText("weekly-core.weekly-email.description");

        if (lunchReminderStage == LunchReminderStage.SECOND_EMAIL_SENT) {
            subject = Mitems.getText("weekly-core.first-reminder-email.subject");
            description = Mitems.getText("weekly-core.first-reminder-email.description");

        } else if (lunchReminderStage == LunchReminderStage.THIRD_EMAIL_SENT) {
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
                String.format("%s/%s?trigger=start-weekly-core", Settings.ARMORY_SITE_URL, armoryConnectionId),
                "armoryUrl2", String.format("%s/%s?trigger=start-lunch-decline-reason-screen",
                        Settings.ARMORY_SITE_URL, armoryConnectionId)));
        SendEmailPayload email = new SendEmailPayload();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(subject);
        email.setHtmlText(htmlBody);
        return email;
    }

    public BaseTemplate availableDaysScreen() {
        Option[] days = Mitems.getOptions("weekly-core.days.each-day");
        List<CloudSelectComponent.Option> options = new ArrayList<>();

        for (Option option : days)
            options.add(new CloudSelectComponent.Option(option.getText(), option.getId(), true));

        BaseTemplate screen = new TemplateGenerator()
                .addComponent("title",
                        new TitleComponent(Mitems.getText("weekly-core.title-asking-for-available-days.title")))
                .addComponent("text",
                        new DescriptionComponent(
                                Mitems.getText("weekly-core.description-asking-for-available-days.text")))
                .addComponent("cloudSelect",
                        new CloudSelectComponent("availableDays", options))
                .addComponent("confirmDays",
                        new PrimarySubmitButtonComponent("confirmDays", "Submit", "confirmDays"));
        return screen;
    }

    public Map<String, BaseTemplate> confirmingDaysScreen() {
        Option buttonOption = Mitems.getOptions("weekly-core.confirmation-of-choosen-available-days.button")[0];
        Map<String, BaseTemplate> screens = Map.of(
                "confirmDaysScreen", new TemplateGenerator("confirmScreen")
                        .setTemplateName("CenteredContentTemplate")
                        .addComponent("title", new TitleComponent(
                                Mitems.getHTML("weekly-core.confirmation-of-choosen-available-days.title")))
                        .addComponent("button", new PrimarySubmitButtonComponent(
                                buttonOption.getText(), buttonOption.getId())),
                "confirmDaysAndThanksScreen", new TemplateGenerator("confirmAndThanksScreen")
                        .setTemplateName("CenteredContentTemplate")
                        .addComponent("title", new TitleComponent(
                                Mitems.getText("weekly-core.stay-tuned-second-confirmation-of-available-days.title"))));
        return screens;
    }

    public Map<String, BaseTemplate> lunchDeclineReasonScreens() {
        Map<String, BaseTemplate> screens = new HashMap<>();
        screens.put("LunchDecline", new TemplateGenerator()
                .addComponent("title", new TitleComponent(
                        Mitems.getText("weekly-core.lunch-decline-reason.title")))
                .addComponent("answer", new TextAreaComponent("answer", true))
                .addComponent("submit", new PrimarySubmitButtonComponent(
                        "finished-lunch-decline-form", "Submit", "finished-lunch-decline-form")));
        screens.put("finished-lunch-decline-form", new TemplateGenerator()
                .addComponent("description", new TitleComponent(
                        Mitems.getText("weekly-core.lunch-decline-reason.final-screen-title"))));
        return screens;
    }

    public SendEmailPayload calendarInviteEmail(Days days, EmployeeProfile currentEmployee,
            EmployeeProfile otherEmployee) throws IOException {

        if (currentEmployee == null || otherEmployee == null)
            throw new RuntimeException("Ava.sendCalendarInvite called with null arguments!");

        String subject = Templating.recursiveRender(Mitems.getText("weekly-core.matching-mail.subject"), Map.of(
                "employeeName", otherEmployee.getFirstName(),
                "day", daysToPrettyString(days)));

        SendEmailPayload payload = new SendEmailPayload();
        payload.setRecipients(List.of(currentEmployee.getEmail()));
        payload.setSubject(subject);
        payload.setHtmlText(renderMatchmakingEmail(days, currentEmployee, otherEmployee));
        payload.setAttachments(
                List.of(new AttachmentData(getICSInvite(days, currentEmployee, otherEmployee), "invite.ics")));
        return payload;
    }

    private String renderMatchmakingEmail(Days days, EmployeeProfile currentEmployee, EmployeeProfile otherEmployee)
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
                "lunchDay", daysToPrettyString(days)));
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

    public SendEmailPayload noMatchEmail(String emailConnectionId) throws IOException {
        SendEmailPayload email = new SendEmailPayload();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(Mitems.getText("weekly-core.no-match-email.subject"));
        email.setPlainText(Mitems.getText("weekly-core.no-match-email.description"));
        return email;
    }

    public BaseTemplate userAlreadyRespondedScreen() {
        BaseTemplate screen = new TemplateGenerator("goodbye")
                .addComponent("title", new TitleComponent(
                        Mitems.getText("weekly-core.user-already-responded-screen.title")));
        return screen;
    }

    public BaseTemplate lunchInviteExpiredScreen() {
        BaseTemplate lunchInviteExpiredScreen = new TemplateGenerator()
                .addComponent("title", new TitleComponent(
                        Mitems.getText("weekly-core.message-about-not-working-hours-for-links.title")));
        return lunchInviteExpiredScreen;
    }
}