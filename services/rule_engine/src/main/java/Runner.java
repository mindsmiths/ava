import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.subscriptions.DataChanges;
import com.mindsmiths.ruleEngine.util.Agents;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Flow;

import agents.CultureMaster;
import models.EmployeeProfile;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        //configureSignals(getClass().getResourceAsStream("config/signals.yaml"));
        configureSignals(DataChanges.on(Employee.class).sendTo(CultureMaster.ID));
        //configureSignals(DataChanges.on(EmployeeProfile.class).sendTo(CultureMaster.ID));
        // Create CultureMaster if he doesn't exist
        if (!Agents.exists(CultureMaster.ID))
            Agents.createAgent(new agents.CultureMaster());
        registerForChanges(Flow.class);
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}