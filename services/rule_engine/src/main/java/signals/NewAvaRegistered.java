package signals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mindsmiths.sdk.core.api.Signal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewAvaRegistered extends Signal{
    String avaId;
}
