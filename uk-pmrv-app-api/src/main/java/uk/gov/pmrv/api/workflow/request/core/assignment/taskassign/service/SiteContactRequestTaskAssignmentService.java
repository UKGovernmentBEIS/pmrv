package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@Service
@Log4j2
@RequiredArgsConstructor
public class SiteContactRequestTaskAssignmentService {

    private final RequestTaskRepository requestTaskRepository;

    private final AccountContactQueryService accountContactQueryService;

    private final RequestTaskAssignmentService requestTaskAssignmentService;

    private final RequestTaskReleaseService requestTaskReleaseService;

    public void assignTasksOfDeletedUserToSiteContactOrRelease(String userDeleted, AccountContactType accountContactType) {
        List<RequestTask> requestTasks =
            requestTaskRepository
                .findByAssigneeAndRequestStatus(userDeleted, RequestStatus.IN_PROGRESS);

        if (!requestTasks.isEmpty()) {
            Set<Long> accountIds =
                requestTasks.stream()
                    .map(RequestTask::getRequest)
                    .map(Request::getAccountId)
                    .collect(Collectors.toSet());

            List<AccountContactInfoDTO> accountVbSiteContacts =
                accountContactQueryService.findContactsByAccountIdsAndContactType(accountIds, accountContactType);

            doAssignTasksToSiteContactOrRelease(requestTasks, accountVbSiteContacts);
        }
    }

    private void doAssignTasksToSiteContactOrRelease(List<RequestTask> requestTasks,
                                                     List<AccountContactInfoDTO> accountSiteContacts) {
        requestTasks.forEach(rt -> {
            AccountContactInfoDTO siteContact =
                findSiteContactByAccountId(accountSiteContacts, rt.getRequest().getAccountId());
            assignTaskToSiteContactOrRelease(rt, siteContact);
        });
    }

    private void assignTaskToSiteContactOrRelease(RequestTask requestTask, AccountContactInfoDTO contact) {
        if (contact != null && !ObjectUtils.isEmpty(contact.getUserId())) {
            try {
                requestTaskAssignmentService.assignToUser(requestTask, contact.getUserId());
            } catch (BusinessCheckedException ex) {
                log.error("Task '{}' cannot be assigned to site contact user '{}'", requestTask::getId,
                    contact::getUserId);
                releaseTask(requestTask);
            }
        } else {
            releaseTask(requestTask);
        }
    }

    private void releaseTask(RequestTask requestTask) {
        try {
            requestTaskReleaseService.releaseTaskForced(requestTask);
        } catch (BusinessException ex) {
            log.error("Cannot release task '{}'. Error message: '{}'", requestTask::getId, ex::getMessage);
        }
    }

    private AccountContactInfoDTO findSiteContactByAccountId(List<AccountContactInfoDTO> accountSiteContacts, Long accountId) {
        return accountSiteContacts.stream()
            .filter(sc -> sc.getAccountId().equals(accountId))
            .findFirst()
            .orElse(null);
    }
}
