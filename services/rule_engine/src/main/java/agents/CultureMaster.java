package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.core.db.DataUtils;
import com.mindsmiths.sdk.core.db.EmitType;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.CmLunchCycleStage;

import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.pairingalgorithm.AvaAvailability;
import com.mindsmiths.pairingalgorithm.Match;

import signals.EmployeeUpdateSignal;
import signals.AllEmployees;
import signals.SendMatchesSignal;

import models.EmployeeProfile;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    private List<AvaAvailability> avaAvailabilities = new ArrayList<>();
    private List<Match> allMatches = new ArrayList<>();
    private CmLunchCycleStage lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
    private Map<String, EmployeeProfile> employees = new HashMap<>();

    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

    public void addAvaAvailability(AvaAvailability avaAvailability) {
        avaAvailabilities.add(avaAvailability);
    }

    public void clearAvaAvailabilities() {
        this.avaAvailabilities = new ArrayList<>();
    }

    public void generateMatches() {
        PairingAlgorithmAPI.generatePairs(new ArrayList<>(avaAvailabilities));
    }

    public void addMatches(List<Match> allMatches) {
        this.allMatches = allMatches;
    }

    private String getFullName(EmployeeProfile employee) {
        return employee.getFirstName() + " " + employee.getLastName();
    }

    public void sendMatches() {
        for (String employeeKey : employees.keySet()) {
            for (Match m : allMatches) {
                if (employeeKey.equals(m.getFirst())) {
                    send(employeeKey, new SendMatchesSignal(m.getSecond(), m.getDay()));
                    break;
                }
                if (employeeKey.equals(m.getSecond())) {
                    send(employeeKey, new SendMatchesSignal(m.getFirst(), m.getDay()));
                    break;
                }
            }
        }
        for (Match m : allMatches)
            DataUtils.emit(m, EmitType.CREATE);
    }

    public void addOrUpdateEmployee(EmployeeUpdateSignal signal) {
        employees.put(signal.getFrom(), signal.getEmployee());
    }

    public void sendEmployeesToAva() {
        int silosCount = calculateSilosCount();
        String silosRisk = calculateSilosRisk(silosCount, employees.size());
        for (String address : employees.keySet()) {
            AllEmployees allEmployees = new AllEmployees(employees, silosCount, silosRisk);
            send(address, allEmployees);
        }
    }

    public boolean[][] calculateEmployeeConnections(Map<String, EmployeeProfile> employees) {

        Double[][] matrix = new Double[employees.values().size()][employees.values().size()]; // weight matrix
        boolean[][] binaryMatrix = new boolean[employees.values().size()][employees.values().size()]; // binary matrix

        List<String> list = new LinkedList<>();
        int limit = 2;

        for (int i = 0; i < employees.values().size(); i++) {
            for (int j = 0; j < employees.values().size(); j++) {
                matrix[i][j] = 0.0;
            }
        }
        int i = 0, j = 0;

        for (EmployeeProfile employee : employees.values()) {
            for (Map.Entry<String, Double> entry : employee.getFamiliarity().entrySet()) {
                if (j == i) {
                    matrix[i][j] = 0.0;
                    binaryMatrix[i][j] = false;
                    j++;
                }

                matrix[i][j] = entry.getValue();
                j++;
            }
            if (i == employees.values().size() - 1) {
                matrix[i][j] = 0.0;
            }

            list.add(employee.getId());
            i++;
            j = 0;
        }

        for (i = 0; i < employees.values().size(); i++) {
            for (j = 0; j < employees.values().size(); j++) {
                Double sum = (matrix[i][j] + matrix[j][i]);
                binaryMatrix[i][j] = sum >= limit;
            }
        }
        return binaryMatrix;
    }

    public void gatherAllConnections(boolean[][] matrix, boolean[] visited, int node) {
        for (int i = 0; i < matrix[0].length; i++) {
            if (matrix[node][i] && !visited[i]) {
                visited[i] = true;
                gatherAllConnections(matrix, visited, i);
            }
        }
    }

    public int calculateSilosCount() {

        boolean[][] matrix = calculateEmployeeConnections(employees);

        int groupCount = 0;
        int size = matrix[0].length;
        boolean[] visited = new boolean[size];

        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                groupCount++;
                visited[i] = true;
                gatherAllConnections(matrix, visited, i);
            }
        }
        return groupCount;
    }

    public String calculateSilosRisk(int silosNum, int employeeNum) {
        double score = (double) silosNum / (double) employeeNum;

        String risk = "";
        if (score <= 0.1)
            risk = "low";
        else if (score < 0.15)
            risk = "moderate";
        else
            risk = "high";

        return risk;
    }
}