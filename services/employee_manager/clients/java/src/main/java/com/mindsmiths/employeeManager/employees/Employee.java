package com.mindsmiths.employeeManager.employees;

import com.mindsmiths.sdk.core.db.DataModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@DataModel(serviceName = "employee-manager")
public class Employee implements Serializable {
    String id;
    String firstName;
    String lastName;
    String email;
    Boolean active;
}