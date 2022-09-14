package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderCreateValidatorTest {

    @InjectMocks
    private PermitSurrenderCreateValidator validator;
    
    @Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private RequestQueryService requestQueryService;
    
    @Test
    void validateAction() {
        Long accountId = 1L;
        
        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.LIVE);
        when(requestQueryService.findOpenRequestsByAccount(accountId)).thenReturn(List.of());
        
        RequestCreateValidationResult result = validator.validateAction(accountId);
        
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        
        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(accountId);
    }
    
    @Test
    void validateAction_validation_failed() {
        Long accountId = 1L;
        
        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.NEW);
        when(requestQueryService.findOpenRequestsByAccount(accountId)).thenReturn(List.of(
                Request.builder().type(RequestType.PERMIT_ISSUANCE).status(RequestStatus.IN_PROGRESS).build(),
                Request.builder().type(RequestType.PERMIT_VARIATION).status(RequestStatus.IN_PROGRESS).build()
                ));
        
        RequestCreateValidationResult result = validator.validateAction(accountId);
        
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(false)
                .reportedAccountStatus(AccountStatus.NEW)
                .applicableAccountStatuses(Set.of(AccountStatus.LIVE))
                .reportedRequestTypes(Set.of(RequestType.PERMIT_VARIATION)).build());
        
        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(accountId);
    }

    @Test
    void type() {
        RequestCreateActionType actionType = validator.getType();

        assertThat(actionType).isEqualTo(RequestCreateActionType.PERMIT_SURRENDER);
    }
}
