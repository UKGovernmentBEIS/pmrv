package uk.gov.pmrv.api.account.service.validator;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.SpELParser;

@Aspect
@Component
@RequiredArgsConstructor
public class AccountStatusAspect {

    private final AccountRepository accountRepository;

    @Before("@annotation(uk.gov.pmrv.api.account.service.validator.AccountStatus)")
    public void validateAccountStatus(JoinPoint joinPoint) {
        String expression = getExpression(joinPoint);
        Long accountId = (Long) joinPoint.getArgs()[0];
        // lock account:
        Account account = accountRepository.findByIdForUpdate(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        final Boolean valid = SpELParser.parseExpression(
            expression,
            new String[] {"status"},
            new Object[] {account.getStatus().name()},
            Boolean.class);

        if (BooleanUtils.isFalse(valid)) {
            throw new BusinessException(ErrorCode.ACCOUNT_INVALID_STATUS);
        }
    }

    private String getExpression(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(AccountStatus.class).expression();
    }

}
