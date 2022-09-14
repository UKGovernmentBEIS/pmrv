package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class AerCreateValidatorServiceTest {

    @InjectMocks
    private AerCreateValidatorService validator;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private AerRequestIdGenerator aerRequestIdGenerator;

    @Test
    void validate() {
        final Year year = Year.now();
        final Long accountId = 1L;
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AerRequestMetadata.builder().type(RequestMetadataType.AER).year(year).build())
                .build();

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.LIVE);

        // Invoke
        final RequestCreateValidationResult result = validator.validate(accountId, year);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(accountId);
        verify(aerRequestIdGenerator, times(1)).generate(params);
    }

    @Test
    void validate_invalid_status() {
        final Year year = Year.now();
        final Long accountId = 1L;
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AerRequestMetadata.builder().type(RequestMetadataType.AER).year(year).build())
                .build();

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.NEW);

        // Invoke
        final RequestCreateValidationResult result = validator.validate(accountId, year);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(false)
                .reportedAccountStatus(AccountStatus.NEW)
                .applicableAccountStatuses(Set.of(AccountStatus.LIVE)).build());
        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(accountId);
        verify(aerRequestIdGenerator, times(1)).generate(params);
    }

    @Test
    void validate_invalid_conflicts() {
        final Year year = Year.now();
        final Long accountId = 1L;
        final String requestId = "requestId";
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AerRequestMetadata.builder().type(RequestMetadataType.AER).year(year).build())
                .build();

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(AccountStatus.LIVE);
        when(requestQueryService.findOpenRequestsByAccount(accountId)).thenReturn(
                List.of(Request.builder().id(requestId).type(RequestType.AER).build())
        );
        when(aerRequestIdGenerator.generate(params)).thenReturn(requestId);

        // Invoke
        final RequestCreateValidationResult result = validator.validate(accountId, year);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder()
                .valid(false)
                .reportedRequestTypes(Set.of(RequestType.AER)).build());
        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findOpenRequestsByAccount(accountId);
        verify(aerRequestIdGenerator, times(1)).generate(params);
    }

    @Test
    void getType() {
        RequestCreateActionType type = validator.getType();

        assertThat(type).isEqualTo(RequestCreateActionType.AER);
    }
}
