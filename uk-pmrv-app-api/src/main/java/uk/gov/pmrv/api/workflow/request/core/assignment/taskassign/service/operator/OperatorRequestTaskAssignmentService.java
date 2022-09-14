package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@Log4j2
@Service
@AllArgsConstructor
public class OperatorRequestTaskAssignmentService implements UserRoleRequestTaskAssignmentService {

    private final RequestTaskRepository requestTaskRepository;
    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final AccountContactQueryService accountContactQueryService;
    private final RequestTaskReleaseService requestTaskReleaseService;

    @Override
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }

    @Transactional
    public void assignTask(RequestTask requestTask, String userId) {
        try {
            requestTaskAssignmentService.assignToUser(requestTask, userId);
        } catch (BusinessCheckedException e) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }
    }

    @Transactional
    public void assignUserTasksToAccountPrimaryContactOrRelease(String userId, Long accountId) throws BusinessCheckedException {
        List<RequestTask> userRequestTasks = requestTaskRepository
            .findByAssigneeAndRequestAccountIdAndRequestStatus(userId, accountId, RequestStatus.IN_PROGRESS);

        if (!userRequestTasks.isEmpty()) {
            String accountPrimaryContact = accountContactQueryService.findPrimaryContactByAccount(accountId);

            userRequestTasks.forEach(requestTask -> assignTaskToAccountPrimaryContactOrRelease(requestTask, accountPrimaryContact));
        }
    }

    private void assignTaskToAccountPrimaryContactOrRelease(RequestTask requestTask, String userId) {
        try {
            requestTaskAssignmentService.assignToUser(requestTask, userId);
        } catch (BusinessCheckedException ex) {
            log.error("Task '{}' cannot be assigned to account primary contact user '{}'", requestTask::getId, () ->userId);
            requestTaskReleaseService.releaseTaskForced(requestTask);
        }
    }
}
