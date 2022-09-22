package models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.mindsmiths.mitems.Mitems;
import com.mindsmiths.mitems.Option;
import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.ruleEngine.util.Log;
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

    public Match(com.mindsmiths.pairingalgorithm.Match match){
        this.firstEmployeeId = match.getFirst();
        this.secondEmployeeId = match.getSecond();
        this.dayOfWeek = daysToPrettyString(match.getDay());
        this.date = nextDayOfWeek(match.getDay());
    }

    public Date nextDayOfWeek(Days dow) {
        Calendar now = Calendar.getInstance();
        
        int diff = dow.ordinal() - now.get(java.util.Calendar.DAY_OF_WEEK);
        if (diff <= 0) 
            diff += 7;

        now.add(java.util.Calendar.DAY_OF_MONTH, diff + 2);
        return now.getTime();
    }

    public String daysToPrettyString(Days days) {
        for(Option option: Mitems.getOptions("weekly-core.days.each-day")) {
            if(days.toString().equals(option.getId())) {
                return option.getText();
            } 
        }       
        return "Unknown";
    }
}