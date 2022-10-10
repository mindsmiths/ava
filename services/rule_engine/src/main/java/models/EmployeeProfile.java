package models;

import com.mindsmiths.employeeManager.employees.Employee;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeProfile extends Employee {
    Map<String, Double> familiarity = new HashMap<>();
    Map<String, String> personalAnswers = new HashMap<>();

    public EmployeeProfile(Employee employee) {
        setId(employee.getId());
        setFirstName(employee.getFirstName());
        setLastName(employee.getLastName());
        setEmail(employee.getEmail());
        setActive(employee.getActive());
    }

    public EmployeeProfile(Map<String, Double> familiarity, Map<String, String> personalAnswers, String id,
            String firstName, String lastName, String email) {
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.familiarity = familiarity;
        this.personalAnswers = personalAnswers;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}