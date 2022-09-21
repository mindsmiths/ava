package com.mindsmiths.employeeManager;

import java.util.Date;

import com.mindsmiths.sdk.core.api.BaseMessage;
import com.mindsmiths.sdk.messaging.Messaging;

public class EmployeeManagerAPI {

    private static final String topic = Messaging.getInputTopicName("employee_manager");
 
    public static void createMatch(String firstEmployeeId, String secondEmployeeId, Date date, String dayOfWeek){
        GenerateMatchPayload payload = new GenerateMatchPayload(firstEmployeeId, secondEmployeeId, date, dayOfWeek);
        BaseMessage message = new BaseMessage("CREATE_MATCH", payload);
        message.send(topic);
    }
}
