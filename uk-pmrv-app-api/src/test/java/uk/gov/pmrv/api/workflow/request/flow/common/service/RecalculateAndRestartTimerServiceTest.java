package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class RecalculateAndRestartTimerServiceTest {

    @InjectMocks
    private RecalculateAndRestartTimerService service;

    @Mock
    private DateService dateService;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Test
    void updateDueDate_whenDueDateIsAfterExpirationDateAndTaskApplicable_thenReturnNewDate() {
        final String requestId = "1";

        final Calendar calendarRfiStart = Calendar.getInstance();
        calendarRfiStart.set(2020, Calendar.JANUARY, 1);
        final Date rfiStart = calendarRfiStart.getTime();

        final Calendar calendarExpiration = Calendar.getInstance();
        calendarExpiration.set(2020, Calendar.FEBRUARY, 1);
        final Date expiration = calendarExpiration.getTime();

        final LocalDate expectedDueDate = LocalDate.of(2020, 2, 5);

        when(dateService.getLocalDateTime()).thenReturn(LocalDateTime.of(2020, 1, 5, 12, 0));

        final LocalDateTime dueDate =
            service.updateDueDate(rfiStart, expiration, requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);

        assertEquals(LocalDateTime.of(expectedDueDate, LocalTime.MIN), dueDate);

        verify(dateService, times(1)).getLocalDateTime();
        verify(requestTaskTimeManagementService, times(1))
            .unpauseTasksAndUpdateDueDate(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, expectedDueDate);
    }

    @Test
    void updateDueDate_whenExpirationDateIsAfterDueDateAndTaskNotApplicable_thenReturnExpirationDate() {
        final String requestId = "1";

        final Calendar calendarRfiStart = Calendar.getInstance();
        calendarRfiStart.set(2020, Calendar.JANUARY, 1);
        final Date rfiStart = calendarRfiStart.getTime();

        final Calendar calendarExpiration = Calendar.getInstance();
        calendarExpiration.set(2020, Calendar.FEBRUARY, 1);
        final Date expiration = calendarExpiration.getTime();

        final LocalDateTime expectedDueDate = LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());

        when(dateService.getLocalDateTime()).thenReturn(LocalDateTime.of(2020, 1, 1, 12, 0));

        final LocalDateTime dueDate =
            service.updateDueDate(rfiStart, expiration, "1", BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);

        assertEquals(expectedDueDate, dueDate);

        verify(dateService, times(1)).getLocalDateTime();
        verify(requestTaskTimeManagementService, times(1))
            .unpauseTasksAndUpdateDueDate(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, expectedDueDate.toLocalDate());
    }
}
