package uk.gov.pmrv.api.workflow.request.core.validation;

import java.util.Optional;
import java.util.Set;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

public abstract class RequestTaskActionAbstractValidator implements RequestTaskActionValidator{

    protected abstract RequestTaskActionValidationResult.ErrorMessage getErrorMessage();

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {

        final Set<RequestTaskType> requestTaskTypes = this.getConflictingRequestTaskTypes();

        Optional<RequestTask> conflictingRequestTask = requestTask.getRequest().getRequestTasks().stream()
            .filter(r -> requestTaskTypes.contains(r.getType()))
            .findFirst();

        return conflictingRequestTask.isEmpty()
            ? RequestTaskActionValidationResult.validResult()
            : RequestTaskActionValidationResult.invalidResult(this.getErrorMessage());
    }
}
