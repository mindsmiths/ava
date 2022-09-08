package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import com.mindsmiths.sdk.core.api.Signal;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class DayChoiceSignal extends Signal {
    ArrayList<String> notFreeDays;    
}
