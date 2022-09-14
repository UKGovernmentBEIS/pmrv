package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@ExtendWith(MockitoExtension.class)
class SiteContactRequestTaskAssignmentServiceTest {

    @InjectMocks
    private SiteContactRequestTaskAssignmentService siteContactRequestTaskAssignmentService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private RequestTaskReleaseService requestTaskReleaseService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Test
    void assignTasksOfDeletedUserToSiteContactOrRelease() throws BusinessCheckedException {
        String userId = "userId";
        String caSiteContact = "caSiteContact";
        Long accountId = 1L;

        Request request = Request.builder().accountId(accountId).status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();
        AccountContactInfoDTO accountContactInfo =
            AccountContactInfoDTO.builder().accountId(accountId).userId(caSiteContact).build();

        when(requestTaskRepository
            .findByAssigneeAndRequestStatus(userId, RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(requestTask));
        when(accountContactQueryService
            .findContactsByAccountIdsAndContactType(Set.of(accountId), AccountContactType.CA_SITE))
            .thenReturn(List.of(accountContactInfo));

        siteContactRequestTaskAssignmentService
            .assignTasksOfDeletedUserToSiteContactOrRelease(userId, AccountContactType.CA_SITE);

        verify(requestTaskRepository, times(1))
            .findByAssigneeAndRequestStatus(userId, RequestStatus.IN_PROGRESS);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoInteractions(requestTaskReleaseService);
    }

    @Test
    void assignTasksOfDeletedUserToSiteContactOrRelease_no_tasks_found_for_user() {
        String userId = "userId";

        when(requestTaskRepository
            .findByAssigneeAndRequestStatus(userId, RequestStatus.IN_PROGRESS))
            .thenReturn(Collections.emptyList());

        siteContactRequestTaskAssignmentService
            .assignTasksOfDeletedUserToSiteContactOrRelease(userId, AccountContactType.CA_SITE);

        verifyNoInteractions(accountContactQueryService, requestTaskAssignmentService, requestTaskReleaseService);
    }

    @Test
    void assignTasksOfDeletedUserToSiteContactOrRelease_no_site_contact() {
        String userId = "userId";
        Long accountId = 1L;

        Request request = Request.builder().accountId(accountId).status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();
        AccountContactInfoDTO accountContactInfo =
            AccountContactInfoDTO.builder().accountId(accountId).build();

        when(requestTaskRepository
            .findByAssigneeAndRequestStatus(userId, RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(requestTask));
        when(accountContactQueryService
            .findContactsByAccountIdsAndContactType(Set.of(accountId), AccountContactType.CA_SITE))
            .thenReturn(List.of(accountContactInfo));

        siteContactRequestTaskAssignmentService
            .assignTasksOfDeletedUserToSiteContactOrRelease(userId, AccountContactType.CA_SITE);

        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
        verifyNoInteractions(requestTaskAssignmentService);
    }

    @Test
    void assignTasksOfDeletedUserToSiteContactOrRelease_task_can_not_be_assigned_to_site_contact()
        throws BusinessCheckedException {
        String userId = "userId";
        String caSiteContact = "caSiteContact";
        Long accountId = 1L;

        Request request = Request.builder().accountId(accountId).status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();
        AccountContactInfoDTO accountContactInfo =
            AccountContactInfoDTO.builder().accountId(accountId).userId(caSiteContact).build();

        when(requestTaskRepository
            .findByAssigneeAndRequestStatus(userId, RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(requestTask));
        when(accountContactQueryService
            .findContactsByAccountIdsAndContactType(Set.of(accountId), AccountContactType.CA_SITE))
            .thenReturn(List.of(accountContactInfo));

        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, caSiteContact);

        siteContactRequestTaskAssignmentService
            .assignTasksOfDeletedUserToSiteContactOrRelease(userId, AccountContactType.CA_SITE);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
    }

    @Test
    void assignTasksOfDeletedUserToSiteContactOrRelease_task_can_not_be_assigned_to_site_contact_nor_released()
        throws BusinessCheckedException {
        String userId = "userId";
        String caSiteContact = "caSiteContact";
        Long accountId = 1L;

        Request request = Request.builder().accountId(accountId).status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();
        AccountContactInfoDTO accountContactInfo =
            AccountContactInfoDTO.builder().accountId(accountId).userId(caSiteContact).build();

        when(requestTaskRepository
            .findByAssigneeAndRequestStatus(userId, RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(requestTask));
        when(accountContactQueryService
            .findContactsByAccountIdsAndContactType(Set.of(accountId), AccountContactType.CA_SITE))
            .thenReturn(List.of(accountContactInfo));
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService)
            .assignToUser(requestTask, caSiteContact);
        doThrow(BusinessException.class).when(requestTaskReleaseService).releaseTaskForced(requestTask);

        siteContactRequestTaskAssignmentService
            .assignTasksOfDeletedUserToSiteContactOrRelease(userId, AccountContactType.CA_SITE);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
    }
}