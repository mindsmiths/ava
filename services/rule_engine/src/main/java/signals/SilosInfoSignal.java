package signals;

import com.mindsmiths.sdk.core.api.Signal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class SilosInfoSignal extends Signal {
    private int silosCount;
    private int silosRisk;

    public SilosInfoSignal(int silosCount) {
        this.silosCount = silosCount;
    }
}