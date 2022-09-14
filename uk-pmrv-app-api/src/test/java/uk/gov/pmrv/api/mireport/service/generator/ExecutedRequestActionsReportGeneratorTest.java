package uk.gov.pmrv.api.mireport.service.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestAction;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestActionsMiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;
import uk.gov.pmrv.api.mireport.repository.ExecutedRequestActionsRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ExecutedRequestActionsReportGeneratorTest {

    @InjectMocks
    private ExecutedRequestActionsReportGenerator generator;

    @Mock
    private ExecutedRequestActionsRepository repository;

    @Test
    void generateMiReport() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.now())
            .build();
        List<ExecutedRequestAction> executedRequestActions = List.of(
            ExecutedRequestAction.builder()
                .accountId(1L)
                .accountName("accountName")
                .accountStatus(AccountStatus.LIVE)
                .accountType(AccountType.INSTALLATION)
                .legalEntityName("legalEntityName")
                .requestId("REQ-1")
                .requestType(RequestType.PERMIT_ISSUANCE)
                .requestStatus(RequestStatus.IN_PROGRESS)
                .requestActionType(RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED)
                .requestActionSubmitter("submitter")
                .requestActionCompletionDate(LocalDateTime.now())
                .build());

        when(repository.findRequestActionsByCaAndParams(competentAuthority, reportParams)).thenReturn(executedRequestActions);

        ExecutedRequestActionsMiReportResult report =
            (ExecutedRequestActionsMiReportResult) generator.generateMiReport(competentAuthority, reportParams);

        assertNotNull(report);
        assertEquals(MiReportType.COMPLETED_WORK, report.getReportType());
        assertThat(report.getActions()).containsExactlyElementsOf(executedRequestActions);
    }

    @Test
    void getReportType() {
        assertThat(generator.getReportType()).isEqualTo(MiReportType.COMPLETED_WORK);
    }
}