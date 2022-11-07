package com.mindsmiths.employeeManager;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMatchPayload implements Serializable {
    private String firstEmployeeId;
    private String secondEmployeeId;
    private LocalDateTime date;
    private String dayOfWeek;
}
