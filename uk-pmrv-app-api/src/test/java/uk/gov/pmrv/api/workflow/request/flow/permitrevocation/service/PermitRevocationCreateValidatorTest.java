package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class PermitRevocationCreateValidatorTest {

    @InjectMocks
    private PermitRevocationCreateValidator validator;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void validateAction_whenValid_thenOk() {

        final Long accountId = 1L;

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.LIVE);

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());

        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(anyLong());
    }

    @Test
    void validateAction_whenInvalidStatus_thenFail() {

        final Long accountId = 1L;

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.NEW);

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(false)
            .reportedAccountStatus(AccountStatus.NEW)
            .applicableAccountStatuses(Set.of(AccountStatus.LIVE)).build());

        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(anyLong());
    }

    @Test
    void validateAction_whenConflicts_thenFail() {

        final Long accountId = 1L;

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.LIVE);
        when(requestQueryService.findOpenRequestsByAccount(accountId)).thenReturn(
            List.of(Request.builder().type(RequestType.PERMIT_REVOCATION).build())
        );

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertThat(result).isEqualTo(RequestCreateValidationResult.builder()
            .valid(false)
            .reportedRequestTypes(Set.of(RequestType.PERMIT_REVOCATION)).build());

        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(accountId);
    }

    @Test
    void type() {

        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.PERMIT_REVOCATION);
    }
}
