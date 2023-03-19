package models;

import com.mindsmiths.sdk.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Neuron {
    private String id = Utils.randomString();

    private double value = 0;
    private double R_in = 1; // resistance
    private double C = 10; // capacity

    private LocalDateTime lastUpdatedAt = Utils.now();

    public Neuron(double R_in, double C) {
        this.R_in = R_in;
        this.C = C;
    }

    public void decay(double amount) {
        this.update(0, amount, 1);
        lastUpdatedAt = Utils.now();
    }

    public void charge(double amount) {
        this.update(1, amount, this.R_in);
    }

    public void discharge(double amount) {
        this.update(-1, amount, this.R_in);
    }


    protected void update(int target, double amount, double resistance) {
        this.value += (target - this.value) * (1 - Math.exp(-amount / (resistance * this.C)));
    }
}
