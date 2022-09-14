package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation.PermitIssuanceReviewDeterminationGrantedAndDecisionsValidator;

@ExtendWith(MockitoExtension.class)
class PermitReviewDeterminationAndDecisionsValidatorServiceTest {

    @InjectMocks
    private PermitReviewDeterminationAndDecisionsValidatorService validatorService;

    @Spy
    private ArrayList<PermitReviewDeterminationAndDecisionsValidator> validators;

    @Mock
    private PermitIssuanceReviewDeterminationGrantedAndDecisionsValidator permitReviewDeterminationGrantedAndDecisionsValidator;

    @BeforeEach
    void setUp() {
        validators.add(permitReviewDeterminationGrantedAndDecisionsValidator);
    }

    @Test
    void isDeterminationAndDecisionsValid() {
        final PermitIssuanceDeterminateable determination = PermitIssuanceGrantDetermination.builder()
                .type(DeterminationType.GRANTED)
                .reason("reason")
                .activationDate(LocalDate.of(2030, 1, 1))
                .build();
        final PermitIssuanceApplicationReviewRequestTaskPayload payload = PermitIssuanceApplicationReviewRequestTaskPayload
                .builder().build();

        when(permitReviewDeterminationGrantedAndDecisionsValidator.getType()).thenReturn(DeterminationType.GRANTED);
        when(permitReviewDeterminationGrantedAndDecisionsValidator.getRequestType()).thenReturn(RequestType.PERMIT_ISSUANCE);
        when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(payload)).thenReturn(true);

        // Invoke
        boolean actual = validatorService.isDeterminationAndDecisionsValid(determination, payload, RequestType.PERMIT_ISSUANCE);

        // Verify
        assertTrue(actual);
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).getType();
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).getRequestType();
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(payload);
    }

    @Test
    void isDeterminationAndDecisionsValid_not_valid() {
        final PermitIssuanceDeterminateable determination = PermitIssuanceGrantDetermination.builder()
                .type(DeterminationType.GRANTED)
                .reason("reason")
                .activationDate(LocalDate.of(2030, 1, 1))
                .build();
        final PermitIssuanceApplicationReviewRequestTaskPayload payload = PermitIssuanceApplicationReviewRequestTaskPayload
                .builder().build();

        when(permitReviewDeterminationGrantedAndDecisionsValidator.getType()).thenReturn(DeterminationType.GRANTED);
        when(permitReviewDeterminationGrantedAndDecisionsValidator.getRequestType()).thenReturn(RequestType.PERMIT_ISSUANCE);
        when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(payload)).thenReturn(false);

        // Invoke
        boolean actual = validatorService.isDeterminationAndDecisionsValid(determination, payload, RequestType.PERMIT_ISSUANCE);

        // Verify
        assertFalse(actual);
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).getType();
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).getRequestType();
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(payload);
    }
}
