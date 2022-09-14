package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionValidatorService;

/**
 * Business Validation Aspect when executing a request task action.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ProcessRequestTaskAspect {

    private final RequestTaskService requestTaskService;
    private final RequestTaskActionValidatorService requestTaskActionValidatorService;

    /**
     * Validates if processing of request task is valid. The task should be opened, its type is an allowed request action
     * and assignable to authenticated user.
     *
     * @param joinPoint {@link JoinPoint} that contains the request task id, the {@link RequestActionType} and the {@link PmrvUser}
     */
    @Before("execution(* uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler.process*(..)) || " +
            "execution(* uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskAttachmentUploadService.uploadAttachment*(..)) || " +
            "execution(* uk.gov.pmrv.api.workflow.request.flow.payment.service.CardPaymentService.createCardPayment*(..)) || " +
            "execution(* uk.gov.pmrv.api.workflow.request.flow.payment.service.CardPaymentService.processExistingCardPayment*(..)) "
            )
    public void validateProcessRequestTask(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        Long requestTaskId = (Long) args[0];
        RequestTaskActionType requestTaskActionType = (RequestTaskActionType) args[1];
        PmrvUser authUser = (PmrvUser) args[2];

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        if(!authUser.getUserId().equals(requestTask.getAssignee())) {
            throw new BusinessException(ErrorCode.REQUEST_TASK_ACTION_USER_NOT_THE_ASSIGNEE);
        }
        
        if(!requestTask.getType().getAllowedRequestTaskActionTypes().contains(requestTaskActionType)){
            throw new BusinessException(ErrorCode.REQUEST_TASK_ACTION_CANNOT_PROCEED);
        }
        requestTaskActionValidatorService.validate(requestTask, requestTaskActionType);
    }
}
