package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationGrantedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceReviewDeterminationGrantedAndDecisionsValidatorTest {
    
    @InjectMocks
    private PermitIssuanceReviewDeterminationGrantedAndDecisionsValidator validator;

    @Mock
    private PermitReviewDeterminationGrantedAndDecisionsValidator<PermitIssuanceReviewDecision> permitReviewDeterminationAndDecisionsGrantedValidator;
    
    @Mock
    private PermitReviewGroupsValidator<PermitIssuanceReviewDecision> permitReviewGroupsValidator;
    
    @Test
    void getType() {
    	assertThat(validator.getType()).isEqualTo(DeterminationType.GRANTED);
    }
    
    @Test
    void getRequestType() {
    	assertThat(validator.getRequestType()).isEqualTo(RequestType.PERMIT_ISSUANCE);
    }
    
    @Test
    void isValid_true() {
    	PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
    	when(permitReviewDeterminationAndDecisionsGrantedValidator.isValid(taskPayload)).thenReturn(true);
    	
    	assertThat(validator.isValid(taskPayload)).isTrue();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verify(permitReviewDeterminationAndDecisionsGrantedValidator, times(1)).isValid(taskPayload);
    }
    
    @Test
    void isValid_false_not_contains_all_mandatory_groups() {
    	PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(false);
    	
    	assertThat(validator.isValid(taskPayload)).isFalse();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verifyNoInteractions(permitReviewDeterminationAndDecisionsGrantedValidator);
    }
    
    @Test
    void isValid_false_granted_determination_not_valid() {
    	PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
    	when(permitReviewDeterminationAndDecisionsGrantedValidator.isValid(taskPayload)).thenReturn(false);
    	
    	assertThat(validator.isValid(taskPayload)).isFalse();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verify(permitReviewDeterminationAndDecisionsGrantedValidator, times(1)).isValid(taskPayload);
    }

}
