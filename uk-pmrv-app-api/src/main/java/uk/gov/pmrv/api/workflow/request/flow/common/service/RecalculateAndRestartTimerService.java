package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.service.DateService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class RecalculateAndRestartTimerService {
    
    private final DateService dateService;
    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    public LocalDateTime updateDueDate(final Date rfiStart, 
                                       final Date reviewExpiration,
                                       final String requestId,
                                       final String expirationDateKey) {

        final LocalDateTime dueDate = this.calculateDueDate(rfiStart, reviewExpiration);
        requestTaskTimeManagementService.unpauseTasksAndUpdateDueDate(requestId, expirationDateKey, dueDate.toLocalDate());
        
        return dueDate;
    }

    private LocalDateTime calculateDueDate(final Date rfiStart, final Date reviewExpiration) {

        final LocalDateTime rfiStartDt = LocalDateTime.ofInstant(rfiStart.toInstant(), ZoneId.systemDefault());
        final LocalDateTime rfiEndDt = dateService.getLocalDateTime();
        final long rfiDuration = DAYS.between(rfiStartDt.toLocalDate(), rfiEndDt.toLocalDate());
        final LocalDateTime reviewExpirationDt = LocalDateTime.ofInstant(reviewExpiration.toInstant(), ZoneId.systemDefault());
        final LocalDateTime dueDateDt = reviewExpirationDt
            .plus(rfiDuration, DAYS)
            .toLocalDate()
            .atTime(LocalTime.MIN);
        
        return reviewExpirationDt.isAfter(dueDateDt) ? reviewExpirationDt : dueDateDt;
    }
}
