package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Ava extends Agent {
    public static String ID = "AVA";

    public Ava() {
        super();
        this.id = Ava.ID;
    }
}