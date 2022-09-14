package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountVbSiteContactService;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;

@ExtendWith(MockitoExtension.class)
class VerifierRequestTaskDefaultAssignmentServiceTest {

    @InjectMocks
    private VerifierRequestTaskDefaultAssignmentService verifierRequestTaskDefaultAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private AccountVbSiteContactService accountVbSiteContactService;

    @Mock
    private RequestReleaseService requestReleaseService;

    @Test
    void assignDefaultAssigneeToTask() throws BusinessCheckedException {
        String vbSiteContact = "vbSiteContact";
        Request request = Request.builder().accountId(1L).status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(accountVbSiteContactService.getVBSiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(vbSiteContact));

        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, vbSiteContact);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_vb_site_contact_not_exists() {
        Request request = Request.builder().accountId(1L).status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(accountVbSiteContactService.getVBSiteContactByAccount(request.getAccountId())).thenReturn(Optional.empty());

        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verifyNoInteractions(requestTaskAssignmentService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_assign_to_vb_site_contact_throws_business_exception() throws BusinessCheckedException {
        String vbSiteContact = "vbSiteContact";
        Request request = Request.builder().accountId(1L).status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(accountVbSiteContactService.getVBSiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(vbSiteContact));
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, vbSiteContact);

        verifierRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, vbSiteContact);
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
    }
}