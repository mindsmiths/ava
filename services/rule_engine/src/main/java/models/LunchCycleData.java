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
    private String id = Utils.randomGenerator();
    private static final int EMAIL_HOUR_DELAY = 12;
    private List<Days> availableDays;
    private String match;
    private Days matchDay;
    private LocalDateTime availableDaysEmailLastSentAt;
    private AvaLunchCycleStage lunchCycleStage;
    private int mailsSent = 0;
    private boolean userResponded;
    private boolean canSendMail;
    
    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for (String day : availableDaysStr)
            this.availableDays.add(Days.valueOf(day));
    }

    public boolean sentMailRecently(LocalDateTime ts){
        if (availableDaysEmailLastSentAt == null) return false;
        return !availableDaysEmailLastSentAt.isBefore(ts.minusHours(EMAIL_HOUR_DELAY));
    }
}
