package uk.gov.pmrv.api.workflow.utils;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

@UtilityClass
public class DateUtils {

    public Long getDaysRemaining(LocalDate pauseDate, LocalDate dueDate) {
        if(!ObjectUtils.isEmpty(dueDate)) {
            pauseDate = ObjectUtils.isEmpty(pauseDate) ? LocalDate.now() : pauseDate;
            return DAYS.between(pauseDate, dueDate);
        }
        return null;
    }

    public Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());
    }
}
