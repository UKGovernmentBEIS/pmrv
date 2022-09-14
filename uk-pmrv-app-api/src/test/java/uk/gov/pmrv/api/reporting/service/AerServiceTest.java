package uk.gov.pmrv.api.reporting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerEntity;
import uk.gov.pmrv.api.reporting.domain.pollutantregistercodes.PollutantRegisterActivities;
import uk.gov.pmrv.api.reporting.domain.pollutantregistercodes.PollutantRegisterActivity;
import uk.gov.pmrv.api.reporting.repository.AerRepository;
import uk.gov.pmrv.api.reporting.validation.AerValidatorService;

@ExtendWith(MockitoExtension.class)
class AerServiceTest {

    @InjectMocks
    private AerService aerService;

    @Mock
    private AerRepository aerRepository;

    @Mock
    private AerValidatorService aerValidatorService;

    @Test
    void submitAer() {
        Long accountId = 14567L;
        Year reportingYear = Year.parse("22/12/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String aerId = "AEM14567-2020";
        Aer aer = Aer.builder()
            .pollutantRegisterActivities(PollutantRegisterActivities.builder()
                .exist(true)
                .activities(Set.of(PollutantRegisterActivity._1_A_2_A_IRON_AND_STEEL))
                .build())
            .build();
        AerContainer aerContainer = AerContainer.builder()
            .aer(aer)
            .build();

        aerService.submitAer(aerContainer, accountId, reportingYear);

        ArgumentCaptor<AerEntity> aerEntityArgumentCaptor = ArgumentCaptor.forClass(AerEntity.class);
        verify(aerRepository, times(1)).save(aerEntityArgumentCaptor.capture());
        AerEntity savedAerEntity = aerEntityArgumentCaptor.getValue();

        assertNotNull(savedAerEntity);
        assertEquals(aerId, savedAerEntity.getId());
        assertEquals(reportingYear, savedAerEntity.getYear());
        assertEquals(accountId, savedAerEntity.getAccountId());
        assertEquals(aerContainer, savedAerEntity.getAerContainer());

        verify(aerValidatorService, times(1)).validate(aerContainer);
    }
}