package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.utils.Utils;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LunchCycleData {
    private String  id = Utils.randomGenerator();
    private List<Days> availableDays = new ArrayList<>();
    private String match;
    private Days matchDay;
    private LocalDateTime availableDaysEmailLastSentAt;
    private AvaLunchCycleStage lunchCycleStage = AvaLunchCycleStage.LUNCH_MAIL_SENDING;
    private LunchReminderStage lunchReminderStage;
    private boolean manualTrigger;
    
    public LunchCycleData(boolean manualTrigger){
        this.manualTrigger = manualTrigger;
    }

    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for (String day : availableDaysStr)
            this.availableDays.add(Days.valueOf(day));
    }
}
