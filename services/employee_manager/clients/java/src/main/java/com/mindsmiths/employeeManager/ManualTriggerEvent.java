package com.mindsmiths.employeeManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.mindsmiths.sdk.core.api.Signal;
import com.mindsmiths.sdk.core.db.DataModel;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@DataModel(serviceName = "employee-manager")
public class ManualTriggerEvent extends Signal {
    String triggerType;    
}
