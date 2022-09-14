package uk.gov.pmrv.api.workflow.request.core.validation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

@Service
@RequiredArgsConstructor
public class RequestTaskActionValidatorService {

    private final List<RequestTaskActionValidator> validators;

    public void validate(final RequestTask requestTask,
                         final RequestTaskActionType requestTaskActionType) {

        List<RequestTaskActionValidationResult> validationResults = new ArrayList<>();

        validators.stream()
            .filter(v -> v.getTypes().contains(requestTaskActionType))
            .forEach(v -> validationResults.add(v.validate(requestTask)));

        boolean isValid = validationResults.stream().allMatch(RequestTaskActionValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(ErrorCode.REQUEST_TASK_ACTION_CANNOT_PROCEED, getValidationErrorMessages(validationResults));
        }
    }

    private Object[] getValidationErrorMessages(List<RequestTaskActionValidationResult> validationResults) {
        return validationResults.stream()
            .filter(validationResult -> !validationResult.isValid())
            .map(RequestTaskActionValidationResult::getErrorMessage)
            .toArray();
    }
}
