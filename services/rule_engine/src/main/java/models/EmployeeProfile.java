package models;

import com.mindsmiths.employeeManager.employees.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor 
@AllArgsConstructor
@Data
public class EmployeeProfile extends Employee {
    Map<String, Double> familiarity = new HashMap<>();
    OnboardingStage onboardingStage;
    
    public EmployeeProfile(Employee employee) {
        setId(employee.getId());
        setFirstName(employee.getFirstName());
        setLastName(employee.getLastName());
        setEmail(employee.getEmail());
        setActive(employee.getActive());
    }

    public EmployeeProfile(Map<String, Double> familiarity, String id, String firstName, String lastName, String email, OnboardingStage onboardingStage) {
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.familiarity = familiarity;
        this.onboardingStage = onboardingStage;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}