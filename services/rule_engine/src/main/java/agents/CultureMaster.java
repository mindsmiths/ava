package agents;

import com.mindsmiths.employeeManager.employees.Employee;
import com.mindsmiths.ruleEngine.model.Agent;
import java.util.*;
import lombok.Data;
import signals.EmployeeUpdateSignal;

@Data
public class CultureMaster extends Agent {
    private List<Map<String, List<Integer>>> freeDays; // information about available days
    private Map<String, Ava> agents; // information about agents
    private Map<String, EmployeeUpdateSignal> avasInfo = new HashMap<>();

    public static String ID = "CULTURE_MASTER";
    public CultureMaster() {
        id = ID;
    }

    public void addOrUpdateAva(String id, EmployeeUpdateSignal employeeSignal) {
        if(!avasInfo.containsKey(id)){
            avasInfo.put(id, employeeSignal);
        }
        else{
            avasInfo.replace(id, employeeSignal);
        }
    }
}
