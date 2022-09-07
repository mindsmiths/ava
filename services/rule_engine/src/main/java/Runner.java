import agents.Ava;
import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;
import com.mindsmiths.mitems.Flow;
import com.mindsmiths.sdk.core.db.DataUtils;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));

        // Create Smith if he doesn't exist
        if (!Agents.exists(Ava.ID))
            Agents.createAgent( new Ava());

        addListener(Flow.class, DataUtils::save);
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}