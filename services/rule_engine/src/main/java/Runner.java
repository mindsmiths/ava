import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;
import com.mindsmiths.mitems.Flow;
import com.mindsmiths.sdk.core.db.DataUtils;

import agents.CultureMaster;
import models.EmployeeProfile;
import agents.Ava;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));

        // Create CultureMaster if he doesn't exist
        if (!Agents.exists(CultureMaster.ID))
            Agents.createAgent(new agents.CultureMaster());

        addListener(Flow.class, DataUtils::save);
        addListener(EmployeeProfile.class, DataUtils::save);
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}