import agents.Ava;
import agents.CultureMaster;

import com.mindsmiths.employeeManager.ManualTriggerEvent;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Flow;
import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.subscriptions.DataChanges;
import com.mindsmiths.ruleEngine.subscriptions.Events;
import com.mindsmiths.ruleEngine.util.Agents;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));
        configureSignals(
            DataChanges.on(Employee.class).sendTo((employee, dataChangeType) ->
                    Agents.getOrCreate(employee.getId(), new Ava())),
            Events.on(ManualTriggerEvent.class).sendTo(CultureMaster.ID),
            Events.on(ManualTriggerEvent.class).sendToAll(Ava.class)
        );

        if (!Agents.exists(CultureMaster.ID))
            Agents.createAgent(new agents.CultureMaster());

        registerForChanges(Flow.class);
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}