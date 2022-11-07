package com.mindsmiths.employeeManager.employees;

import com.mindsmiths.sdk.core.db.DataModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;


@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@DataModel(emit = true)
public class Employee implements Serializable {
    String id;
    String firstName;
    String lastName;
    String email;
    Boolean active;
}