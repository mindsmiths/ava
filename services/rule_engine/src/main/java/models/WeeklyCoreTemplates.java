package models;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.sdk.utils.templating.Templating;

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
}