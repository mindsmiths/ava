package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AvaRegisteredSignal extends Signal{
    String avaId;
}
