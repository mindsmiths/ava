package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualTriggerEvent extends Signal {
    String triggerType;    
}
