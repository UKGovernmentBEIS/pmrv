package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountVbSiteContactService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerifierRequestTaskDefaultAssignmentService implements UserRoleRequestTaskDefaultAssignmentService {

    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final AccountVbSiteContactService accountVbSiteContactService;
    private final RequestReleaseService requestReleaseService;

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }

    @Transactional
    public void assignDefaultAssigneeToTask(RequestTask requestTask) {
        accountVbSiteContactService
            .getVBSiteContactByAccount(requestTask.getRequest().getAccountId())
            .ifPresent(
                vbSiteContactUserId -> {
                    try {
                        requestTaskAssignmentService.assignToUser(requestTask, vbSiteContactUserId);
                    } catch (BusinessCheckedException e) {
                        log.error("Request task '{}' for verifier user will remain unassigned. Error msg : '{}'" ,
                            requestTask::getId, e::getMessage);
                        requestReleaseService.releaseRequest(requestTask);
                    }
                }
            );
    }
}
