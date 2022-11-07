package com.mindsmiths.employeeManager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.mindsmiths.sdk.core.api.Event;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ManualTriggerEvent extends Event {
    String triggerType;    
}
