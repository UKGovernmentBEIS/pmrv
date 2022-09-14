package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.regulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class RegulatorRequestTaskDefaultAssignmentServiceTest {

    @InjectMocks
    private RegulatorRequestTaskDefaultAssignmentService regulatorRequestTaskDefaultAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private AccountCaSiteContactService accountCaSiteContactService;

    @Mock
    private RequestReleaseService requestReleaseService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Test
    void assignDefaultAssigneeToTask_peer_review_task() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestRegulatorPeerReviewer = "requestRegulatorPeerReviewer";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .regulatorPeerReviewer(requestRegulatorPeerReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorPeerReviewer)).thenReturn(true);

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorPeerReviewer);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, requestRegulatorPeerReviewer);
        verifyNoInteractions(accountCaSiteContactService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_peer_review_task_assignment_throws_exception() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestRegulatorPeerReviewer = "requestRegulatorPeerReviewer";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .regulatorPeerReviewer(requestRegulatorPeerReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorPeerReviewer)).thenReturn(true);
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, requestRegulatorPeerReviewer);
        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorPeerReviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, requestRegulatorPeerReviewer);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_peer_review_task_peer_reviewer_not_exists() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(userRoleTypeService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_peer_review_task_peer_reviewer_not_regulator() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestRegulatorPeerReviewer = "requestRegulatorPeerReviewer";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .regulatorPeerReviewer(requestRegulatorPeerReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorPeerReviewer)).thenReturn(false);
        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorPeerReviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_peer_review_task_nor_peer_reviewer_neither_site_contact_exist() {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.empty());

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
        verifyNoInteractions(userRoleTypeService, requestTaskAssignmentService);
    }

    @Test
    void assignDefaultAssigneeToTask_peer_review_task_peer_reviewer_not_exist_site_contact_is_regulator_reviewer() {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String caSiteContact = "requestRegulatorReviewer";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verifyNoInteractions(userRoleTypeService, requestTaskAssignmentService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestRegulatorPeerReviewer = "requestRegulatorPeerReviewer";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .regulatorPeerReviewer(requestRegulatorPeerReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorAssignee)).thenReturn(true);

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, requestRegulatorAssignee);
        verifyNoInteractions(accountCaSiteContactService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_assignment_throws_exception() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestRegulatorPeerReviewer = "requestRegulatorPeerReviewer";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .regulatorPeerReviewer(requestRegulatorPeerReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorAssignee)).thenReturn(true);
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, requestRegulatorAssignee);
        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorAssignee);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, requestRegulatorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_regulator_assignee_not_exists() throws BusinessCheckedException {
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestRegulatorPeerReviewer = "requestRegulatorPeerReviewer";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorReviewer(requestRegulatorReviewer)
                .regulatorPeerReviewer(requestRegulatorPeerReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(userRoleTypeService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_regulator_assignee_not_reviewer() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestRegulatorPeerReviewer = "requestRegulatorPeerReviewer";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .regulatorPeerReviewer(requestRegulatorPeerReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorAssignee)).thenReturn(false);
        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorAssignee);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_nor_regulator_assignee_neither_site_contact_exist() {
        String regulatorPeerReviewer = "regulatorPeerReviewer";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorPeerReviewer(regulatorPeerReviewer)
                .regulatorReviewer(requestRegulatorReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.empty());

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
        verifyNoInteractions(userRoleTypeService, requestTaskAssignmentService);
    }

    @Test
    void assignDefaultAssigneeToTask_assign_to_ca_site_contact_throws_business_exception() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, caSiteContact);

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
        verifyNoInteractions(userRoleTypeService);
    }

    @Test
    void getRoleType() {
        assertEquals(REGULATOR, regulatorRequestTaskDefaultAssignmentService.getRoleType());
    }
}