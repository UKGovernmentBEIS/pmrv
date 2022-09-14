package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceReviewDeterminationDeemedWithdrawnAndDecisionsValidatorTest {

    @InjectMocks
    private PermitIssuanceReviewDeterminationDeemedWithdrawnAndDecisionsValidator validator;

    @Test
    void isValid() {
        final PermitIssuanceApplicationReviewRequestTaskPayload payload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permit(Permit.builder().build())
                .reviewGroupDecisions(Map.of(
                        PermitReviewGroup.CONFIDENTIALITY_STATEMENT,
                        PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                )).build();

        final boolean validate = validator.isValid(payload);
        assertTrue(validate);
    }
    
    @Test
    void getType() {
    	assertThat(validator.getType()).isEqualTo(DeterminationType.DEEMED_WITHDRAWN);
    }
    
    @Test
    void getRequestType() {
    	assertThat(validator.getRequestType()).isEqualTo(RequestType.PERMIT_ISSUANCE);
    }
}