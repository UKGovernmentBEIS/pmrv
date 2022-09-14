package uk.gov.pmrv.api.workflow.request.flow.permitvariation.validation;

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
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewDeterminationGrantedAndDecisionsValidatorTest {
    
    @InjectMocks
    private PermitVariationReviewDeterminationGrantedAndDecisionsValidator validator;

    @Mock
    private PermitReviewDeterminationGrantedAndDecisionsValidator<PermitVariationReviewDecision> permitReviewDeterminationGrantedAndDecisionsValidator;
    
    @Mock
    private PermitReviewGroupsValidator<PermitVariationReviewDecision> permitReviewGroupsValidator;
    
    @Test
    void getType() {
    	assertThat(validator.getType()).isEqualTo(DeterminationType.GRANTED);
    }
    
    @Test
    void getRequestType() {
    	assertThat(validator.getRequestType()).isEqualTo(RequestType.PERMIT_VARIATION);
    }
    
    @Test
    void isValid_true() {
    	PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build())
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
    	when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload)).thenReturn(true);
    	
    	assertThat(validator.isValid(taskPayload)).isTrue();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(taskPayload);
    }
    
    @Test
    void isValid_false_not_contains_all_mandatory_groups() {
    	PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build())
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(false);
    	
    	assertThat(validator.isValid(taskPayload)).isFalse();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verifyNoInteractions(permitReviewDeterminationGrantedAndDecisionsValidator);
    }
    
    @Test
    void isValid_false_not_contains_variation_details_decision() {
    	PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
    	
    	assertThat(validator.isValid(taskPayload)).isFalse();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verifyNoInteractions(permitReviewDeterminationGrantedAndDecisionsValidator);
    }
    
    @Test
    void isValid_false_granted_determination_not_valid() {
    	PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build())
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
    	when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload)).thenReturn(false);
    	
    	assertThat(validator.isValid(taskPayload)).isFalse();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(taskPayload);
    }
    
    @Test
    void isValid_false_variation_details_is_not_accepted() {
    	PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
    					))
    			.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder().type(ReviewDecisionType.REJECTED).build())
    			.build();
    	
    	when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
    	when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload)).thenReturn(true);
    	
    	assertThat(validator.isValid(taskPayload)).isFalse();
    	
    	verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
    	verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(taskPayload);
    }

}
