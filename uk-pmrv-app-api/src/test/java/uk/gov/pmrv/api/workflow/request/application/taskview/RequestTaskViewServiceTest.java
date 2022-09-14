package uk.gov.pmrv.api.workflow.request.application.taskview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.pmrv.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

@ExtendWith(MockitoExtension.class)
class RequestTaskViewServiceTest {

	@InjectMocks
	private RequestTaskViewService service;

	@Mock
    private RequestTaskService requestTaskService;

	@Mock
    private UserService userService;

	@Mock
	private RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

	@Test
    void getTaskItemInfo() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final PmrvUser pmrvUser = PmrvUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleType.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, pmrvUser.getUserId(),
            "proceTaskId", requestTaskType);

        final ApplicationUserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).competentAuthority(ca).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userService.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria))
            .thenReturn(true);
        when(requestTaskAuthorizationResourceService.hasUserExecuteScopeOnRequestTaskType(pmrvUser, requestTask.getType().name(), resourceCriteria))
            .thenReturn(true);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, pmrvUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEqualTo(requestTaskType.getAllowedRequestTaskActionTypes());
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestTask().getAssigneeUserId()).isEqualTo(user);
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userService, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserExecuteScopeOnRequestTaskType(pmrvUser, requestTaskType.name(), resourceCriteria);
    }

	@Test
    void getTaskItemInfo__user_has_not_assign_scope_on_request_tasks() {
	    final String user = "user";
	    final Long accountId = 1L;
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final PmrvUser pmrvUser = PmrvUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleType.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, "another_user",
            "proceTaskId", requestTaskType);

        final ApplicationUserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).competentAuthority(ca).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userService.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria))
            .thenReturn(false);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, pmrvUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isFalse();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userService, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria);
        verifyNoMoreInteractions(requestTaskAuthorizationResourceService);
    }

    @Test
    void getTaskItemInfo_user_is_not_task_assignee() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final PmrvUser pmrvUser = PmrvUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleType.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, "assignee",
            "proceTaskId", requestTaskType);

        final ApplicationUserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).competentAuthority(ca).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userService.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria))
            .thenReturn(true);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, pmrvUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userService, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria);
        verifyNoMoreInteractions(requestTaskAuthorizationResourceService);
    }

    @Test
    void getTaskItemInfo_user_has_not_execute_scope_on_request_task() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final PmrvUser pmrvUser = PmrvUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleType.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, pmrvUser.getUserId(),
            "proceTaskId", requestTaskType);

        final ApplicationUserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).competentAuthority(ca).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userService.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria))
            .thenReturn(true);
        when(requestTaskAuthorizationResourceService.hasUserExecuteScopeOnRequestTaskType(pmrvUser, requestTask.getType().name(), resourceCriteria))
            .thenReturn(false);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, pmrvUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userService, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(pmrvUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserExecuteScopeOnRequestTaskType(pmrvUser, requestTaskType.name(), resourceCriteria);
    }

	private Request createRequest(String requestId, CompetentAuthority ca,
                                  Long accountId, RequestType requestType) {
        return Request.builder()
        			.id(requestId)
        			.type(requestType)
        			.competentAuthority(ca)
        			.status(RequestStatus.IN_PROGRESS)
        			.processInstanceId("procInst")
        			.accountId(accountId)
        			.creationDate(LocalDateTime.now())
        			.build();
    }

    private RequestTask createRequestTask(Long requestTaskId, Request request, String assignee, String processTaskId,
            RequestTaskType requestTaskType) {
        return RequestTask.builder()
	            .id(requestTaskId)
	            .request(request)
	            .processTaskId(processTaskId)
	            .type(requestTaskType)
	            .assignee(assignee)
                .dueDate(LocalDate.now().plusDays(14))
	            .build();
    }
}
