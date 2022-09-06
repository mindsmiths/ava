import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;
import com.mindsmiths.ruleEngine.util.Signals;

import agents.Panic;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml")); 

        if (!Agents.exists(Panic.ID))
            Agents.createAgent(new Panic());
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}
