package com.mindsmiths.employeeManager.employees;

import com.mindsmiths.sdk.core.db.DataModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@DataModel(serviceName = "employee-manager")
public class Employee implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
}