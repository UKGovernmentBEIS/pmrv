package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.service.InstallationAccountOpeningCreateValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderCreateValidator;

@ExtendWith(MockitoExtension.class)
class ProcessRequestCreateAspectTest {

    @InjectMocks
    private ProcessRequestCreateAspect aspect;
    
    @Mock
    private PermitSurrenderCreateValidator permitSurrenderCreateValidator;

    @Mock
    private InstallationAccountOpeningCreateValidator installationAccountOpeningCreateValidator;

    @Mock
    private AccountQueryService accountQueryService;

    @Spy
    private ArrayList<RequestCreateValidator> requestCreateValidators;
    
    @Mock
    private JoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        requestCreateValidators.add(permitSurrenderCreateValidator);
        requestCreateValidators.add(installationAccountOpeningCreateValidator);
    }
    
    @Test
    void process() {
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_SURRENDER;
        final Object[] arguments = new Object[] {
                accountId, type
        };
        
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();
        
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(permitSurrenderCreateValidator.getType()).thenReturn(type);
        when(permitSurrenderCreateValidator.validateAction(accountId)).thenReturn(validationResult);
        
        aspect.process(joinPoint);
        
        verify(joinPoint, times(1)).getArgs();
        verify(permitSurrenderCreateValidator, times(1)).getType();
        verify(permitSurrenderCreateValidator, times(1)).validateAction(accountId);
        verify(accountQueryService, times(1)).exclusiveLockAccount(accountId);
    }
    
    @Test
    void process_validation_failed() {
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_SURRENDER;
        final Object[] arguments = new Object[] {
                accountId, type
        };
        
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder()
                .valid(false)
                .reportedAccountStatus(AccountStatus.NEW)
                .reportedRequestTypes(Set.of(RequestType.INSTALLATION_ACCOUNT_OPENING))
                .build();
        
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(permitSurrenderCreateValidator.getType()).thenReturn(type);
        when(permitSurrenderCreateValidator.validateAction(accountId)).thenReturn(validationResult);
        
        BusinessException be = assertThrows(BusinessException.class, () -> aspect.process(joinPoint));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED);
        assertThat(be.getData()[0]).isEqualTo(validationResult);
        
        verify(joinPoint, times(1)).getArgs();
        verify(permitSurrenderCreateValidator, times(1)).getType();
        verify(permitSurrenderCreateValidator, times(1)).validateAction(accountId);
        verify(accountQueryService, times(1)).exclusiveLockAccount(accountId);
    }

    @Test
    void process_no_account() {
        final RequestCreateActionType type = RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION;
        final Object[] arguments = new Object[] {
                null, type
        };

        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(joinPoint.getArgs()).thenReturn(arguments);
        when(installationAccountOpeningCreateValidator.getType()).thenReturn(type);
        when(installationAccountOpeningCreateValidator.validateAction(null)).thenReturn(validationResult);

        aspect.process(joinPoint);

        verify(joinPoint, times(1)).getArgs();
        verify(installationAccountOpeningCreateValidator, times(1)).getType();
        verify(installationAccountOpeningCreateValidator, times(1)).validateAction(null);
        verify(accountQueryService, never()).exclusiveLockAccount(anyLong());
    }
}
