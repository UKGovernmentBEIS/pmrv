package uk.gov.pmrv.api.workflow.request.core.validation;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@Service
public class PaymentTaskExistenceRequestTaskActionValidator extends RequestTaskActionAbstractValidator {

    @Override
    public Set<RequestTaskActionType> getTypes() {
        Set<RequestTaskActionType> requestTaskActionTypes = new HashSet<>();
        requestTaskActionTypes.addAll(RequestTaskActionType.getNotifyOperatorForDecisionTypes());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRequestPeerReviewTypes());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRfiRdeSubmissionTypes());

        requestTaskActionTypes.add(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION);

        return requestTaskActionTypes;
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return RequestTaskType.getRegulatorPaymentTypes();
    }

    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.PAYMENT_IN_PROGRESS;
    }
}
