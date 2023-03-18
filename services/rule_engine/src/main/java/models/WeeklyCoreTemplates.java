package models;

import com.mindsmiths.armory.Screen;
import com.mindsmiths.armory.component.*;
import com.mindsmiths.emailAdapter.NewEmail;
import com.mindsmiths.emailAdapter.api.AttachmentData;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.ruleEngine.util.Log;
import com.mindsmiths.sdk.utils.Utils;
import com.mindsmiths.sdk.utils.templating.Templating;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import utils.Settings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WeeklyCoreTemplates {

    public static NewEmail weeklyEmail(Employee employee, int numberOfMailsSent, String armoryConnectionId,
                                       String emailConnectionId) throws IOException {
        String emailSlug = "";
        switch (numberOfMailsSent) {
            case 0 -> emailSlug = "weekly-email";
            case 1 -> emailSlug = "second-reminder-email";
            case 2 -> emailSlug = "third-reminder-email";
        }
        Log.warn("Number of mails sent: " + numberOfMailsSent);
        String subject = Mitems.getText("weekly-core." + emailSlug + ".subject");
        String description = Mitems.getText("weekly-core." + emailSlug + ".description");

        String htmlTemplate = new String(Objects.requireNonNull(WeeklyCoreTemplates.class.getClassLoader()
                .getResourceAsStream("emailTemplates/WeeklyEmailTemplate.html")).readAllBytes());
        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
                "text", description,
                "firstName", employee.getFirstName(),
                "button1", Mitems.getText("weekly-core.weekly-email.button1"),
                "button2", Mitems.getText("weekly-core.weekly-email.button2"),
                "armoryUrl1",
                String.format("%s/%s?trigger=start-weekly-core", Settings.ARMORY_SITE_URL, armoryConnectionId),
                "armoryUrl2", String.format("%s/%s?trigger=start-lunch-decline-reason-screen",
                        Settings.ARMORY_SITE_URL, armoryConnectionId),
                "now", Utils.datetimeToStr(Utils.now())));

        NewEmail email = new NewEmail();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(subject);
        email.setHtmlText(htmlBody);
        return email;
    }

    public static Screen availableDaysScreen() {
        Option[] days = Mitems.getOptions("weekly-core.days.each-day");
        List<CloudSelect.Option> options = new ArrayList<>();

        for (Option option : days)
            options.add(new CloudSelect.Option(option.getText(), option.getId(), true));

        return new Screen("availableDaysScreen")
                .add(new Title(Mitems.getText("weekly-core.title-asking-for-available-days.title")))
                .add(new Description(Mitems.getText("weekly-core.description-asking-for-available-days.text")))
                .add(new CloudSelect("availableDays", options))
                .add(new SubmitButton("confirmDays", "Submit"));
    }

    public static List<Screen> confirmingDaysScreen() {
        Option buttonOption = Mitems.getOptions("weekly-core.confirmation-of-choosen-available-days.button")[0];
        return List.of(
                new Screen("confirmScreen")
                        .add(new Title(Mitems.getHTML("weekly-core.confirmation-of-choosen-available-days.title")))
                        .add(new SubmitButton(buttonOption.getId(), buttonOption.getText(), "confirmDays")),
                new Screen("confirmDays")
                        .setTemplate("CenteredContent")
                        .add(new Title(Mitems.getText("weekly-core.stay-tuned-second-confirmation-of-available-days.title"))));
    }

    public static List<Screen> lunchDeclineReasonScreens() {
        return List.of(
                new Screen("LunchDecline")
                        .add(new Title(Mitems.getText("weekly-core.lunch-decline-reason.title")))
                        .add(new TextArea("answer", true))
                        .add(new SubmitButton("finished-lunch-decline-form", "Submit", "finishedLunchDeclineForm")),
                new Screen("finishedLunchDeclineForm")
                        .setTemplate("CenteredContent")
                        .add(new Title(Mitems.getText("weekly-core.lunch-decline-reason.final-screen-title"))));
    }

    public static NewEmail calendarInviteEmail(Days days, Employee currentEmployee,
                                               Employee otherEmployee) throws IOException {

        if (currentEmployee == null || otherEmployee == null)
            throw new RuntimeException("Ava.sendCalendarInvite called with null arguments!");

        String subject = Templating.recursiveRender(Mitems.getText("weekly-core.matching-mail.subject"), Map.of(
                "employeeName", otherEmployee.getFirstName(),
                "day", daysToPrettyString(days)));

        NewEmail payload = new NewEmail();
        payload.setRecipients(List.of(currentEmployee.getEmail()));
        payload.setSubject(subject);
        payload.setHtmlText(renderMatchmakingEmail(days, currentEmployee, otherEmployee));
        payload.setAttachments(
                List.of(new AttachmentData(getICSInvite(days, currentEmployee, otherEmployee), "invite.ics")));
        return payload;
    }

    private static String renderMatchmakingEmail(Days days, Employee currentEmployee, Employee otherEmployee)
            throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(WeeklyCoreTemplates.class.getClassLoader().
                getResourceAsStream("emailTemplates/EmailTemplateCalendar.html")).readAllBytes());
        return Templating.recursiveRender(htmlTemplate, Map.of(
                "title", Mitems.getText("weekly-core.matching-mail.title"),
                "description", Mitems.getHTML("weekly-core.matching-mail.description"),
                "otherName", otherEmployee.getFirstName(),
                "fullName", String.join(" ", otherEmployee.getFirstName(), otherEmployee.getLastName()),
                "myName", currentEmployee.getFirstName(),
                "lunchDay", daysToPrettyString(days),
                "now", Utils.datetimeToStr(Utils.now())));
    }

    private static byte[] getICSInvite(Days day, Employee currentEmployee, Employee otherEmployee) {
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

    public static String daysToPrettyString(Days days) {
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

    public static NewEmail noMatchEmail(String emailConnectionId) {
        NewEmail email = new NewEmail();
        email.setRecipients(List.of(emailConnectionId));
        email.setSubject(Mitems.getText("weekly-core.no-match-email.subject"));
        email.setPlainText(Mitems.getText("weekly-core.no-match-email.description"));
        return email;
    }

    public static Screen userAlreadyRespondedScreen() {
        return new Screen("goodbye")
                .setTemplate("CenteredContent")
                .add(new Title(Mitems.getText("weekly-core.user-already-responded-screen.title")));
    }

    public static Screen lunchInviteExpiredScreen() {
        return new Screen("expiredInvite")
                .setTemplate("CenteredContent")
                .add(new Title(Mitems.getText("weekly-core.message-about-not-working-hours-for-links.title")));
    }
}