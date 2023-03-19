package models;

import com.mindsmiths.sdk.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyCoreData {
    private String id = Utils.randomString();
    private MonthlyCoreStage monthlyCoreStage = MonthlyCoreStage.EMAIL_SENT;
}
