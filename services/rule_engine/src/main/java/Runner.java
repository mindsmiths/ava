import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;
import com.mindsmiths.ruleEngine.util.Signals;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml")); 
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}
