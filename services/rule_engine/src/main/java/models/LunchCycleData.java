package models;

import com.mindsmiths.pairingalgorithm.Days;
import com.mindsmiths.sdk.core.db.DataModel;
import com.mindsmiths.sdk.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DataModel
public class LunchCycleData implements Serializable {
    private static final int EMAIL_HOUR_DELAY = 12;
    private String id = Utils.randomGenerator();
    private List<Days> availableDays;
    private LocalDateTime availableDaysEmailLastSentAt;
    private int mailsSent = 0;
    private boolean canSendMail;
    private boolean userResponded;
    private boolean noDaysSelected;

    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for (String day : availableDaysStr)
            this.availableDays.add(Days.valueOf(day));
    }

    public boolean sentMailRecently(LocalDateTime timestamp) {
        if (availableDaysEmailLastSentAt == null) return false;
        return ChronoUnit.HOURS.between(timestamp, availableDaysEmailLastSentAt) < EMAIL_HOUR_DELAY;
    }
}
