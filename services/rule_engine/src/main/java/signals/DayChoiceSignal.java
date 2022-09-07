package signals;

import java.util.ArrayList;

import com.mindsmiths.sdk.core.api.Signal;

import lombok.Data;

@Data
public class DayChoiceSignal extends Signal {
    ArrayList<Integer> freeDays;

    public DayChoiceSignal(ArrayList<Integer> freeDays) {
    }
    
}
