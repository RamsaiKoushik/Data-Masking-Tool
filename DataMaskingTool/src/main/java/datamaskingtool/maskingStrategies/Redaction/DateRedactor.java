package datamaskingtool.maskingStrategies.Redaction;

import java.util.Calendar;
import java.util.Date;

public class DateRedactor implements Redactor<Date> {

    @Override
    public Date redactValue(Date value, boolean fullRedaction) {
        if (value == null) {
            return null;
        }

        if (fullRedaction) {
            return new Date(0); 
        }

        return partiallyRedact(value);
    }

    @Override
    public Date partiallyRedact(Date value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return (Date) calendar.getTime();
    }
}
