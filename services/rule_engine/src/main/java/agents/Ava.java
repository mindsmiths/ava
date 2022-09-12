package agents;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.pairingalgorithm.AvaAvailability;
import com.mindsmiths.pairingalgorithm.Days;

@Data
@ToString
@NoArgsConstructor
public class Ava extends Agent {
    private Set<Days> availableDays = new HashSet<>();
    private AvaAvailability avaAvailability;
    private String matchName;
    private Days matchDay;

    public Ava(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    //---------TESTING----------------------
    public void randomizeAvailableDays() {
        this.availableDays = new HashSet<>();
        for(int i=0; i<5; i++) {
            int day = (int)((Math.random() * 4));
            switch(day){
                case 0:
                    if(!availableDays.contains(Days.MON)) {
                        availableDays.add(Days.MON);
                    }
                    break;
                case 1:
                    if(!availableDays.contains(Days.TUE)) {
                        availableDays.add(Days.TUE);
                    }
                    break;
                case 2:
                    if(!availableDays.contains(Days.WED)) {
                        availableDays.add(Days.WED);
                    }
                    break;
                case 3:
                    if(!availableDays.contains(Days.THU)) {
                        availableDays.add(Days.THU);
                    }
                    break;
                default:
                    if(!availableDays.contains(Days.FRI)) {
                        availableDays.add(Days.FRI);
                    }
                    break;
            }
        }
    }

    public void updateAvaAvailability() {
        randomizeAvailableDays();
        this.avaAvailability = new AvaAvailability(this.getId(), availableDays);
    }
    //-------------------------------------

}
