package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import java.util.List;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidator;

@Aspect
@Component
@RequiredArgsConstructor
public class ProcessRequestCreateAspect {
    
    private final List<RequestCreateValidator> requestCreateValidators;
    private final AccountQueryService accountQueryService;

    @Before("execution(* uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler.process*(..))")
    public void process(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        final Long accountId = (Long) args[0];
        final RequestCreateActionType type = (RequestCreateActionType)args[1];
        
        Optional<RequestCreateValidator> requestCreateValidatorOpt = requestCreateValidators.stream()
                .filter(validator -> validator.getType() == type).findFirst();
        
        if(requestCreateValidatorOpt.isEmpty()) {
            return;
        }

        // lock the account
        if(accountId != null){
            accountQueryService.exclusiveLockAccount(accountId);
        }
        
        RequestCreateValidationResult validationResult = requestCreateValidatorOpt.get().validateAction(accountId);
        if(!validationResult.isValid()) {
            throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, validationResult);
        }
    }
}
