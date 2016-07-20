package services.date;

import org.joda.time.DateTime;

import java.time.Instant;

public class SystemDateService extends DateService {
    @Override
    public DateTime currentDateTime() {
        return DateTime.now();
    }
}