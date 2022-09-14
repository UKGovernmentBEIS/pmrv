package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.regulator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegulatorRequestTaskDefaultAssignmentService implements UserRoleRequestTaskDefaultAssignmentService {

    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final AccountCaSiteContactService accountCaSiteContactService;
    private final UserRoleTypeService userRoleTypeService;
    private final RequestReleaseService requestReleaseService;

    @Override
    public RoleType getRoleType() {
        return RoleType.REGULATOR;
    }

    @Transactional
    public void assignDefaultAssigneeToTask(RequestTask requestTask) {
        boolean isPeerReviewTask = RequestTaskType.getPeerReviewTypes().contains(requestTask.getType());
        RequestPayload requestPayload = requestTask.getRequest().getPayload();
        String candidateAssignee = isPeerReviewTask ? requestPayload.getRegulatorPeerReviewer() : requestPayload.getRegulatorAssignee();

        if(!ObjectUtils.isEmpty(candidateAssignee) && userRoleTypeService.isUserRegulator(candidateAssignee)) {
            try {
                requestTaskAssignmentService.assignToUser(requestTask, candidateAssignee);
            } catch (BusinessCheckedException e) {
                assignTaskToCASiteContactOrReleaseRequest(requestTask, isPeerReviewTask);
            }
        } else {
            assignTaskToCASiteContactOrReleaseRequest(requestTask, isPeerReviewTask);
        }
    }

    private void assignTaskToCASiteContactOrReleaseRequest(RequestTask requestTask, boolean isPeerReviewTask) {
        accountCaSiteContactService
            .findCASiteContactByAccount(requestTask.getRequest().getAccountId())
            .ifPresentOrElse(
                caSiteContactUser -> {
                    try {
                        if(isPeerReviewTask) {
                            String firstReviewer = requestTask.getRequest().getPayload().getRegulatorReviewer();
                            if (!caSiteContactUser.equals(firstReviewer)) {
                                requestTaskAssignmentService.assignToUser(requestTask, caSiteContactUser);
                            }
                        } else {
                            requestTaskAssignmentService.assignToUser(requestTask, caSiteContactUser);
                        }
                    } catch (BusinessCheckedException e) {
                        log.error("Request task '{}' for regulator user will remain unassigned. Error msg : '{}'" ,
                            requestTask::getId, e::getMessage);
                        requestReleaseService.releaseRequest(requestTask);
                    }
                },
                () -> requestReleaseService.releaseRequest(requestTask)
            );
    }
}
