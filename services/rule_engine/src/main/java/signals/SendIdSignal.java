package signals;

import com.mindsmiths.sdk.core.api.Signal;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendIdSignal extends Signal{
    String id;
}
