import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;
import com.mindsmiths.ruleEngine.util.Signals;
import com.mindsmiths.mitems.Flow;
import com.mindsmiths.sdk.core.db.DataUtils;
import com.mindsmiths.pairingalgorithm.LunchCompatibilities;

import agents.CultureMaster;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));
        configureSignals(Signals.on(LunchCompatibilities.class).sendTo(CultureMaster.ID));
        // Create CultureMaster if he doesn't exist
        if (!Agents.exists(CultureMaster.ID))
            Agents.createAgent(new agents.CultureMaster());

        addListener(Flow.class, DataUtils::save);
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}