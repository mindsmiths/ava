package models;

import com.mindsmiths.ruleEngine.util.DateUtil;
import utils.Settings;

import java.time.LocalDateTime;

public enum CronTime {
    WORKING_HOURS("* * 8-18 ? * MON-FRI *"),
    AVAILABILITY_INTERVAL("* * 8-23 ? * WED", "* * * ? * THU", "* * 0-15 ? * FRI"),
    AFTER_AVAILABILITY_INTERVAL("* * 15 ? * FRI *"),
    PAIRING_INTERVAL("* * 16 ? * FRI *"),
    FIRST_TUESDAY_IN_MONTH("* * * ? * 2#1 *");
    public final String[] value;

    CronTime(String... value) {
        this.value = value;
    }

    public boolean isSatisfied(LocalDateTime now) {

        for (String cron : value)
            if (DateUtil.evaluateCronExpression(cron, now, Settings.DEFAULT_TIME_ZONE))
                return true;

        return false;
    }
}
