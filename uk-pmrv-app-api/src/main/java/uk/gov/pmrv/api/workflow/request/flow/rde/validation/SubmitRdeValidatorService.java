package uk.gov.pmrv.api.workflow.request.flow.rde.validation;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.WorkflowUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;

@Service
@RequiredArgsConstructor
public class SubmitRdeValidatorService {

    private final WorkflowUsersValidator workflowUsersValidator;

    public void validate(final RequestTask requestTask,
                         final RdePayload rdePayload,
                         final PmrvUser pmrvUser) {

        final Long accountId = requestTask.getRequest().getAccountId();

        if(rdePayload.getExtensionDate().isBefore(requestTask.getDueDate())
                || rdePayload.getDeadline().isAfter(rdePayload.getExtensionDate())
                ||!workflowUsersValidator.areOperatorsValid(accountId, rdePayload.getOperators(), pmrvUser)
                || !workflowUsersValidator.isSignatoryValid(requestTask, rdePayload.getSignatory())){
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
