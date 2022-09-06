package agents;

import java.util.List;

import com.mindsmiths.emailAdapter.EmailAdapterAPI;
import com.mindsmiths.emailAdapter.SendEmailPayload;
import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Smith extends Agent {
    public static String Id;

    public Smith() {
        this.Id = "SMITH";
    }

    public void sendEmail(String recipient, String subject, String text) {
        SendEmailPayload e = new SendEmailPayload();
        e.setRecipients(List.of(recipient));
        e.setSubject(subject);
        e.setPlainText(text);
        EmailAdapterAPI.newEmail(e);
    }
}
