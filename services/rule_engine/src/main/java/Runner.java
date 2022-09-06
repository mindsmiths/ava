import agents.Smith;
import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));

        // Create Smith if he doesn't exist
        if (!Agents.exists(Smith.Id))
            Agents.createAgent(new Smith());
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}