package models;

import java.io.Serializable;
import java.util.Date;

import com.mindsmiths.sdk.core.db.DataModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DataModel(serviceName = "rule_engine")
public class Match implements Serializable {
    private String firstEmployeeId;
    private String secondEmployeeId;
    private String dayOfWeek;
    private Date date;
}