package signals;

import java.util.ArrayList;

import lombok.AllArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;


@AllArgsConstructor
public class DayChoiceSignal extends Signal {
    ArrayList<Integer> freeDays;    
}
