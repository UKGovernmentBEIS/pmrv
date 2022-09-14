package uk.gov.pmrv.api.mireport.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.mireport.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportSearchResult;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;
import uk.gov.pmrv.api.mireport.repository.MiReportRepository;
import uk.gov.pmrv.api.mireport.service.generator.AccountsUsersContactsReportGenerator;

@ExtendWith(MockitoExtension.class)
public class MiReportServiceTest {

    private MiReportService service;

    @Mock
    private MiReportRepository miReportRepository;

    @Mock
    private AccountsUsersContactsReportGenerator accountsUsersContactsReportGenerator;

    @BeforeEach
    void setup() {
        service = new MiReportService(miReportRepository, List.of(accountsUsersContactsReportGenerator));
    }

    @Test
    public void findByCompetentAuthority() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        MiReportSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSearchResult.class);

        when(miReportRepository.findByCompetentAuthority(competentAuthority)).thenReturn(List.of(expectedMiReportSearchResult));

        List<MiReportSearchResult> actual = service.findByCompetentAuthority(competentAuthority);

        assertThat(actual.get(0)).isEqualTo(expectedMiReportSearchResult);
    }

    @Test
    public void generateReport() {
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();
        MiReportResult expectedMiReportResult = mock(MiReportResult.class);

        when(accountsUsersContactsReportGenerator.getReportType()).thenReturn(reportType);
        when(accountsUsersContactsReportGenerator.generateMiReport(pmrvUser.getCompetentAuthority(), reportParams)).thenReturn(
            expectedMiReportResult);

        MiReportResult actualMiReportResult = service.generateReport(pmrvUser, reportParams);

        assertThat(actualMiReportResult).isEqualTo(expectedMiReportResult);
    }

    @Test
    public void generateReport_generator_not_found() {
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        MiReportType reportType = MiReportType.COMPLETED_WORK;
        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder().reportType(reportType).build();

        when(accountsUsersContactsReportGenerator.getReportType()).thenReturn(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS);

        BusinessException businessException = assertThrows(BusinessException.class, () -> service.generateReport(pmrvUser, reportParams));

        assertEquals(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED, businessException.getErrorCode());

        verify(accountsUsersContactsReportGenerator, times(0)).generateMiReport(pmrvUser.getCompetentAuthority(), reportParams);
    }
}
