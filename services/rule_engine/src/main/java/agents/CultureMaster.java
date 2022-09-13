package agents;

import java.time.LocalDateTime;
import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import signals.EmployeeUpdateSignal;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.utils.templating.Templating;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    
    private List<Map<String, List<Integer>>> freeDays; // information about available days
    Map<String, Ava> agentInfo; // information about agents
    private Date lastEmailSentTime = new Date();
    private boolean pinged = false;
    private Map<String, Employee> employees = new HashMap<>();

    public static String ID = "CULTURE_MASTER";
    public CultureMaster() {
        id = ID;
    }
    
    public void addOrUpdateEmployee(EmployeeUpdateSignal signal){
        employees.put(signal.getFrom(), signal.getEmployee());
    }
}
