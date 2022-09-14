package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountAuthorizationResourceService;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountRequestAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

@Testcontainers
@SpringBootTest(
    properties = {
        "camunda.bpm.enabled=false"
    }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ParallelRequestHandlingIT extends AbstractContainerBaseTest {

    private static final String DELETE_REASON = "Workflow terminated by the system";
    private static final Long TEST_ACCOUNT_ID = 1L;
    private static final String TEST_REQUEST_ID = "1";
    private static final String TEST_PROCESS_ID = "2";
    @Autowired
    private AccountStatusService accountStatusService;
    @Autowired
    private AccountRepository repository;
    @Autowired
    private RequestRepository requestRepository;
    @MockBean
    WorkflowService workflowService;
    @MockBean
    UserAuthService userAuthService;
    @MockBean
    AccountAuthorizationResourceService accountAuthorizationResourceService;
    @MockBean
    AccountRequestAuthorizationResourceService accountRequestAuthorizationResourceService;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        requestRepository.deleteAll();
        repository.save(
            Account.builder()
                .id(TEST_ACCOUNT_ID)
                .accountType(AccountType.INSTALLATION)
                .commencementDate(LocalDate.now())
                .competentAuthority(CompetentAuthority.ENGLAND)
                .verificationBodyId(TEST_ACCOUNT_ID)
                .status(AccountStatus.LIVE)
                .name("account1")
                .siteName("account1")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM00001")
                .build()
        );
        requestRepository.save(
            Request.builder()
                .id(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .processInstanceId(TEST_PROCESS_ID)
                .type(RequestType.PERMIT_REVOCATION)
                .status(RequestStatus.IN_PROGRESS)
                .creationDate(LocalDateTime.now())
                .build()
        );
    }

    @Test
    void shouldHandlePermitRevocationInProgress() {

        accountStatusService.handlePermitSurrenderGranted(TEST_ACCOUNT_ID);

        Account account = repository.findById(TEST_ACCOUNT_ID).get();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.AWAITING_SURRENDER);

        Request request = requestRepository.findById(TEST_REQUEST_ID).get();
        assertThat(request.getStatus()).isEqualTo(RequestStatus.CANCELLED);

        RequestAction closeRequestAction = request.getRequestActions().get(0);
        assertThat(closeRequestAction.getType()).isEqualTo(RequestActionType.REQUEST_TERMINATED);
//        assertThat(closeRequestAction.getPayload()).isNull();
        assertThat(closeRequestAction.getSubmitter()).isNull();

        verify(workflowService, times(1)).deleteProcessInstance(TEST_PROCESS_ID, DELETE_REASON);
    }

    @Test
    void shouldRollbackWhenEventListenerFails() {
        // given the listener throws
        doThrow(new RuntimeException()).when(workflowService).deleteProcessInstance(TEST_PROCESS_ID, DELETE_REASON);
        // when permit surrender is granted
        assertThrows(RuntimeException.class, () -> accountStatusService.handlePermitSurrenderGranted(TEST_ACCOUNT_ID));
        // then the account has the original status
        Account account = repository.findById(TEST_ACCOUNT_ID).get();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.LIVE);
        // and the request has the original status
        Request request = requestRepository.findById(TEST_REQUEST_ID).get();
        assertThat(request.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        // and no request action has been recorded
        assertThat(request.getRequestActions()).hasSize(0);
    }
}
