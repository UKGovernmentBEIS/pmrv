package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Year;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AerDueDateServiceTest {

    @InjectMocks
    private AerDueDateService aerDueDateService;

    @Test
    void generateDueDate() {
        Date expectedDate = Timestamp.valueOf(LocalDate.of(Year.now().getValue(), 4, 1).atTime(0, 0));
        // Invoke
        Date actualDate = aerDueDateService.generateDueDate();

        // Verify
        assertThat(actualDate).isEqualTo(expectedDate);
    }
}
