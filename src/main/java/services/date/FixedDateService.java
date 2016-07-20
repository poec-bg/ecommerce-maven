package services.date;

import org.joda.time.DateTime;

import java.time.Instant;

public abstract class FixedDateService extends DateService {

    public abstract DateTime currentDateTime() ;

}