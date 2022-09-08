import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;

import java.util.ArrayList;
import java.util.List;

import com.mindsmiths.mitems.Flow;
import com.mindsmiths.sdk.core.db.DataUtils;

import agents.CultureMaster;



public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));

        // Create CultureMaster if he doesn't exist
        if (!Agents.exists(CultureMaster.ID))
            Agents.createAgent(new agents.CultureMaster());

        addListener(Flow.class, DataUtils::save);

        //--------------TESTING---------------------
        for (int i=0; i<6; i++) {
            Agents.createAgent(new agents.Ava());
        }
        //------------------------------------------
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}