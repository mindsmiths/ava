package com.mindsmiths.employeeManager;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMatchPayload implements Serializable {
    private String firstEmployeeId;
    private String secondEmployeeId;
    private Date date;
    private String dayOfWeek;
}
