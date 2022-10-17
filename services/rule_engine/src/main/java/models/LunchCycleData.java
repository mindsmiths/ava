package models;

import java.util.ArrayList;
import java.util.List;

import com.mindsmiths.pairingalgorithm.Days;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LunchCycleData {
    private String id;
    private List<Days> availableDays = new ArrayList<>();
    private String match;
    private Days matchDay;


    public LunchCycleData(String id){
        this.id = id;
    }

    public LunchCycleData(String id, List<Days> availableDays){
        this.id = id;
        this.availableDays = availableDays;
    }

    public void updateAvailableDays(List<String> availableDaysStr) {
        this.availableDays = new ArrayList<>();
        for (String day : availableDaysStr)
            this.availableDays.add(Days.valueOf(day));
    }
}

