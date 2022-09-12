import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.util.Agents;
import com.mindsmiths.mitems.Flow;
import com.mindsmiths.sdk.core.db.DataUtils;
import com.mindsmiths.employeeManager.employees.Employee;

import agents.CultureMaster;
import agents.Ava;

public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));

        // Create CultureMaster if he doesn't exist
        if (!Agents.exists(CultureMaster.ID))
            Agents.createAgent(new agents.CultureMaster());

        // test
            Ava ava = new Ava("email", "tomislav.matic01@gmail.com");
            ava.addConnection("armory", "tomislav");
            Agents.createAgent(ava);
        // test

        addListener(Flow.class, DataUtils::save);
        addListener(Employee.class, DataUtils::save);
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }

}