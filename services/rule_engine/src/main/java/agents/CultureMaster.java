package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindsmiths.pairingalgorithm.EmployeeAvailability;
import com.mindsmiths.pairingalgorithm.Match;
import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.sdk.core.db.DataUtils;
import com.mindsmiths.sdk.core.db.EmitType;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.CmLunchCycleStage;

import com.mindsmiths.pairingalgorithm.PairingAlgorithmAPI;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.pairingalgorithm.Match;
import com.mindsmiths.ruleEngine.util.Log;

import signals.EmployeeUpdateSignal;
import signals.AllEmployees;
import signals.SendMatchesSignal;

import models.EmployeeProfile;
import signals.AllEmployees;
import signals.EmployeeUpdateSignal;
import signals.SendMatchesSignal;
import signals.SendNoMatchesSignal;

@Data
@AllArgsConstructor
public class CultureMaster extends Agent {
    private List<EmployeeAvailability> employeeAvailabilities = new ArrayList<>();
    private List<Match> allMatches = new ArrayList<>();
    private CmLunchCycleStage lunchCycleStage = CmLunchCycleStage.COLLECT_AVA_AVAILABILITIES;
    private Map<String, EmployeeProfile> employees = new HashMap<>();
    private Map<String, Map<String, Double>> employeeConnectionStrengths = new HashMap<>();

    public static String ID = "CULTURE_MASTER";

    public CultureMaster() {
        id = ID;
    }

    public void addEmployeeAvailability(EmployeeAvailability employeeAvailability) {
        employeeAvailabilities.add(employeeAvailability);
    }

    public void clearEmployeeAvailabilities() {
        this.employeeAvailabilities = new ArrayList<>();
    }

    public void generateMatches() {
        PairingAlgorithmAPI.generatePairs(new ArrayList<>(employeeAvailabilities),
                new HashMap<>(employeeConnectionStrengths));
    }

    public void addMatches(List<Match> allMatches) {
        this.allMatches = allMatches;
    }

    public void sendMatches() {
        List<String> matchedPeople = new ArrayList<>();
        for (String employeeKey : employees.keySet()) {
            for (Match m : allMatches) {
                if (employees.get(employeeKey).getId().equals(m.getFirst())) {
                    matchedPeople.add(m.getFirst());
                    break;
                }
                if (employees.get(employeeKey).getId().equals(m.getSecond())) {
                    matchedPeople.add(m.getSecond());
                    break;
                }
            }
        }
        for (String employeeKey : employees.keySet()) {
            for (Match m : allMatches) {
                if (!matchedPeople.contains(employees.get(employeeKey).getId())) {
                    send(employeeKey, new SendNoMatchesSignal());
                    break;
                }
                if (employees.get(employeeKey).getId().equals(m.getFirst())) {
                    send(employeeKey, new SendMatchesSignal(m.getSecond(), m.getDay()));
                    break;
                }
                if (employees.get(employeeKey).getId().equals(m.getSecond())) {
                    send(employeeKey, new SendMatchesSignal(m.getFirst(), m.getDay()));
                    break;
                }
            }
        }
        for (Match m : allMatches) {
            DataUtils.emit(new models.Match(m), EmitType.CREATE);
        }

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
        int limit = 1;

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
                binaryMatrix[i][j] = (matrix[i][j] > limit) && (matrix[j][i] > limit);
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