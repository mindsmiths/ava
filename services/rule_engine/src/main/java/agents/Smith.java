package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Smith extends Agent {
    public static String ID = "SMITH";

    public Smith() {
    }
    
}