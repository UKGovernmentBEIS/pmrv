package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

public interface UserRoleRequestTaskDefaultAssignmentService {

    void assignDefaultAssigneeToTask(RequestTask requestTask);
    RoleType getRoleType();
}
