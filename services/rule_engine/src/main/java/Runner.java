import agents.CultureMaster;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.mitems.Flow;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.ruleEngine.runner.RuleEngineService;
import com.mindsmiths.ruleEngine.subscriptions.DataChanges;
import com.mindsmiths.ruleEngine.util.Agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Runner extends RuleEngineService {
    @Override
    public void initialize() {
        configureSignals(getClass().getResourceAsStream("config/signals.yaml"));
        configureSignals(
                DataChanges.on(Employee.class).sendTo((employee, dataChangeType) -> getAgentsForEmployeeUpdate(employee))
        );

        if (!Agents.exists(CultureMaster.ID))
            Agents.createAgent(new agents.CultureMaster());

        registerForChanges(Flow.class);
    }

    public static List<String> getAgentsForEmployeeUpdate(Employee employee) {
        List<String> agents = new ArrayList<>(Collections.singleton(CultureMaster.ID));
        agents.addAll(Agents.getByConnection("employeeId",
                employee.getId()).stream().map(Agent::getId).toList());
        return agents;
    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.start();
    }
}