package agents;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.utils.templating.Templating;

import signals.DayChoiceSignal;
import utils.Settings;

@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent { 

    private boolean workingHours;
    private boolean welcomeEmailSent;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void sendData(ArrayList<Integer> freeDays) {
        send("CultureMaster", new DayChoiceSignal(freeDays));
    }

    public void sendWelcomeEmail() throws IOException {
        String subject = Mitems.getText("onboarding.welcome-email.subject");
        String description = Mitems.getText("onboarding.welcome-email.description");
        String htmlTemplate = String.join("", Files.readAllLines(Paths.get("EmailTemplate.html"), StandardCharsets.UTF_8));

        String htmlBody = Templating.recursiveRender(htmlTemplate, Map.of(
            "description", description,
            "callToAction", "Let's go",
            "firstName", "Juraj",
            "armoryUrl", String.format("%s/%s", Settings.ARMORY_SITE_URL, getConnection("armory"))
        ));

        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(getConnection("email")));
        e.setSubject(subject);
        e.setHtmlText(htmlBody);
        EmailAdapterAPI.newEmail(e);
    }
 
}
