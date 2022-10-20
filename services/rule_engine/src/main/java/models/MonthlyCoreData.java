package models;

import lombok.Data;

import com.mindsmiths.sdk.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyCoreData {
    private String  id = Utils.randomGenerator();
    private MonthlyCoreStage monthlyCoreStage = MonthlyCoreStage.EMAIL_SENT;
    private boolean manualTrigger;
    
    public MonthlyCoreData(boolean manualTrigger){
        this.manualTrigger = manualTrigger;
    }
}
