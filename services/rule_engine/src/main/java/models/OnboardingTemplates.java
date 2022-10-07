package models;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.ruleEngine.util.Log;
import com.mindsmiths.sdk.utils.templating.Templating;

import utils.Settings;

public class OnboardingTemplates {
    
    public SendEmailPayload welcomeEmail(EmployeeProfile employee, String armoryConnectionId, String emailConnectionId) throws IOException {
        String htmlTemplate = new String(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(
                    "emailTemplates/EmailTemplate.html")).readAllBytes());
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
}