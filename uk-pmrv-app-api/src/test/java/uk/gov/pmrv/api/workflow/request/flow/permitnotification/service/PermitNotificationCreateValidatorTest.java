package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationCreateValidatorTest {

    @InjectMocks
    private PermitNotificationCreateValidator validator;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void validateAction() {
        Long accountId = 1L;

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.LIVE);

        RequestCreateValidationResult result = validator.validateAction(accountId);

        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());

        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, never()).findOpenRequestsByAccount(anyLong());
    }

    @Test
    void validateAction_validation_failed() {
        Long accountId = 1L;

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.NEW);

        RequestCreateValidationResult result = validator.validateAction(accountId);

        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(false)
                .reportedAccountStatus(AccountStatus.NEW)
                .applicableAccountStatuses(Set.of(
                        AccountStatus.LIVE,
                        AccountStatus.AWAITING_SURRENDER,
                        AccountStatus.SURRENDERED,
                        AccountStatus.AWAITING_REVOCATION,
                        AccountStatus.REVOKED,
                        AccountStatus.AWAITING_TRANSFER,
                        AccountStatus.TRANSFERRED,
                        AccountStatus.RATIONALISED,
                        AccountStatus.AWAITING_RATIONALISATION
                )).build());

        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, never()).findOpenRequestsByAccount(anyLong());
    }

    @Test
    void type() {
        RequestCreateActionType actionType = validator.getType();

        assertThat(actionType).isEqualTo(RequestCreateActionType.PERMIT_NOTIFICATION);
    }
}
