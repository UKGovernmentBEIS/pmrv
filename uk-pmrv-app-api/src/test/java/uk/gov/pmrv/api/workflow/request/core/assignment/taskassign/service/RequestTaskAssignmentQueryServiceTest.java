package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.pmrv.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class RequestTaskAssignmentQueryServiceTest {

    @InjectMocks
    private RequestTaskAssignmentQueryService requestTaskAssignmentQueryService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Mock
    private RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    @Test
    void getCandidateAssigneesByTaskId_non_peer_review_task() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthority ca = ENGLAND;
        final RoleType userRole = OPERATOR;
        final String requestRegulatorReviewer = "requestRegulatorReviewer";
        Request request = Request.builder()
            .accountId(accountId)
            .competentAuthority(ca)
            .payload(PermitIssuanceRequestPayload.builder().regulatorReviewer(requestRegulatorReviewer).build())
            .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
                .request(request).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();
        PmrvUser user = PmrvUser.builder().roleType(userRole).build();
        List<String> candidateAssignees = List.of("userId1", "userId2");
        List<UserInfo> users = buildMockUserInfoList(candidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(candidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).competentAuthority(ca).build();
        
        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
                requestTask.getType().name(), resourceCriteria, userRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, times(1))
                .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTask.getType().name(), resourceCriteria, userRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskId_peer_review_task() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthority ca = ENGLAND;
        final RoleType userRole = REGULATOR;
        final String requestRegulatorReviewer = "requestRegulatorReviewer";
        final String userId1 = "userId1";
        final String userId2 = "userId2";
        Request request = Request.builder()
            .accountId(accountId)
            .competentAuthority(ca)
            .payload(PermitIssuanceRequestPayload.builder().regulatorReviewer(requestRegulatorReviewer).build())
            .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
            .request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();
        PmrvUser user = PmrvUser.builder().roleType(userRole).build();
        List<String> candidateAssignees = new ArrayList<>(Arrays.asList(requestRegulatorReviewer, userId1, userId2));
        List<String> peerReviewTaskCandidateAssignees = new ArrayList<>(Arrays.asList(userId1, userId2));
        List<UserInfo> users = buildMockUserInfoList(peerReviewTaskCandidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(peerReviewTaskCandidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).competentAuthority(ca).build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTask.getType().name(), resourceCriteria, userRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, times(1))
            .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTask.getType().name(), resourceCriteria, userRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskId_not_valid_task_capability() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthority ca = ENGLAND;
        Request request = Request.builder().accountId(accountId).competentAuthority(ca).build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
                .request(request).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();
        PmrvUser user = PmrvUser.builder().roleType(OPERATOR).build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED))
                .when(requestTaskAssignmentValidationService).validateTaskAssignmentCapability(requestTask);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user));

        // Assert
        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, never())
                .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(anyString(), any(), any());
        verify(userAuthService, never()).getUsers(anyList());
    }

    @Test
    void getCandidateAssigneesByTaskId_empty_users() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthority ca = ENGLAND;
        Request request = Request.builder().accountId(accountId).competentAuthority(ca).build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
                .request(request).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();
        PmrvUser user = PmrvUser.builder().roleType(OPERATOR).build();
        List<String> candidateAssignees = List.of();
        List<UserInfo> users = List.of();
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = List.of();
        
        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).competentAuthority(ca).build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
                requestTask.getType().name(), resourceCriteria, OPERATOR)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, times(1))
                .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTask.getType().name(), resourceCriteria, OPERATOR);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskType_peer_review_task() {
        final RequestTaskType requestTaskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;
        final CompetentAuthority ca = ENGLAND;
        final RoleType pmrvUserRole = REGULATOR;
        final String pmrvUserId = "userId";
        final String userId1 = "userId1";
        final String userId2 = "userId2";
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .competentAuthority(ca)
            .build();
        PmrvUser pmrvUser = PmrvUser.builder().userId(pmrvUserId).roleType(pmrvUserRole).authorities(List.of(pmrvAuthority)).build();
        List<String> candidateAssignees = new ArrayList<>(Arrays.asList(pmrvUserId, userId1, userId2));
        List<String> peerReviewTaskCandidateAssignees = new ArrayList<>(Arrays.asList(userId1, userId2));
        List<UserInfo> users = buildMockUserInfoList(peerReviewTaskCandidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(peerReviewTaskCandidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().competentAuthority(ca).build();

        // Mock
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTaskType.name(), resourceCriteria, pmrvUserRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService
            .getCandidateAssigneesByTaskType(requestTaskType, pmrvUser);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTaskType);
        verify(requestTaskAuthorizationResourceService, times(1))
            .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType.name(), resourceCriteria, pmrvUserRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskType_non_peer_review_task() {
        final RequestTaskType requestTaskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
        final CompetentAuthority ca = ENGLAND;
        final RoleType pmrvUserRole = REGULATOR;
        final String pmrvUserId = "userId";
        final String userId1 = "userId1";
        final String userId2 = "userId2";
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .competentAuthority(ca)
            .build();
        PmrvUser pmrvUser = PmrvUser.builder().userId(pmrvUserId).roleType(pmrvUserRole).authorities(List.of(pmrvAuthority)).build();
        List<String> candidateAssignees = new ArrayList<>(Arrays.asList(pmrvUserId, userId1, userId2));
        List<UserInfo> users = buildMockUserInfoList(candidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(candidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().competentAuthority(ca).build();

        // Mock
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTaskType.name(), resourceCriteria, pmrvUserRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService
            .getCandidateAssigneesByTaskType(requestTaskType, pmrvUser);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTaskType);
        verify(requestTaskAuthorizationResourceService, times(1))
            .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType.name(), resourceCriteria, pmrvUserRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskType_task_type_not_assignable() {
        final RequestTaskType requestTaskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;
        final String pmrvUserId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(pmrvUserId).build();

        // Mock
        doThrow(new BusinessException(ErrorCode.REQUEST_TASK_NOT_ASSIGNABLE))
            .when(requestTaskAssignmentValidationService).validateTaskAssignmentCapability(requestTaskType);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
            requestTaskAssignmentQueryService.getCandidateAssigneesByTaskType(requestTaskType, pmrvUser));

        // Assert
        assertEquals(ErrorCode.REQUEST_TASK_NOT_ASSIGNABLE, businessException.getErrorCode());
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTaskType);
        verifyNoInteractions(requestTaskAuthorizationResourceService, userAuthService);
    }

    private List<UserInfo> buildMockUserInfoList(List<String> userIds) {
        return userIds.stream()
                .map(userId -> UserInfo.builder().id(userId).firstName(userId).lastName(userId).build())
                .collect(Collectors.toList());
    }

    private List<AssigneeUserInfoDTO> buildMockAssigneeUserInfoList(List<String> userIds) {
        return userIds.stream()
                .map(userId -> AssigneeUserInfoDTO.builder().id(userId).firstName(userId).lastName(userId).build())
                .collect(Collectors.toList());
    }
}
