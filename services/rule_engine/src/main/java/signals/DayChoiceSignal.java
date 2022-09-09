package signals;

import java.util.List;

import com.mindsmiths.sdk.core.api.Signal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class DayChoiceSignal extends Signal {
    List<String> notFreeDays;    
}
