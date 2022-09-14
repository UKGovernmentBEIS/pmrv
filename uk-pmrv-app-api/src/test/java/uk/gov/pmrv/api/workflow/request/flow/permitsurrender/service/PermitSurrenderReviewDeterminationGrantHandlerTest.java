package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewDeterminationGrantHandlerTest {

    @InjectMocks
    private PermitSurrenderReviewDeterminationGrantHandler handler;
    
    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(PermitSurrenderReviewDeterminationType.GRANTED);
    }
    
    @Test
    void handleDeterminationUponDecision_should_not_reset_if_decision_is_accepted() {
        PermitSurrenderReviewDetermination reviewDetermination = PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .stopDate(LocalDate.now().minusDays(1))
                .build();
        
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("notes").build())
                .reviewDetermination(reviewDetermination)
                .build();
        
        PermitSurrenderReviewDecision newDecision = PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("new notes").build();
        
        handler.handleDeterminationUponDecision(taskPayload, newDecision);
        
        assertThat(taskPayload.getReviewDetermination()).isEqualTo(reviewDetermination);
    }
    
    @Test
    void handleDeterminationUponDecision_should_reset_if_decision_is_not_accepted() {
        PermitSurrenderReviewDetermination reviewDetermination = PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .stopDate(LocalDate.now().minusDays(1))
                .build();
        
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("notes").build())
                .reviewDetermination(reviewDetermination)
                .build();
        
        PermitSurrenderReviewDecision newDecision = PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.REJECTED).notes("new notes").build();
        
        handler.handleDeterminationUponDecision(taskPayload, newDecision);
        
        assertThat(taskPayload.getReviewDetermination()).isNull();
    }
    
    @Test
    void validateDecisionUponDetermination_should_throw_error_if_decision_is_missing() {
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .build();
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> handler.validateDecisionUponDetermination(taskPayload));
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }
    
    @Test
    void validateDecisionUponDetermination_should_throw_error_if_decision_is_not_accepted() {
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.REJECTED).notes("notes").build())
                .build();
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> handler.validateDecisionUponDetermination(taskPayload));
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }
    
    @Test
    void validateReview() {
        PermitSurrenderReviewDecision reviewDecision = PermitSurrenderReviewDecision.builder()
                .type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("new notes").build();
        PermitSurrenderReviewDeterminationGrant reviewDetermination = PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .stopDate(LocalDate.now().minusDays(1))
                .build();
        
        handler.validateReview(reviewDecision, reviewDetermination);
    }
    
    @Test
    void validateReview_invalid() {
        PermitSurrenderReviewDecision reviewDecision = PermitSurrenderReviewDecision.builder()
                .type(PermitSurrenderReviewDecisionType.REJECTED).notes("new notes").build();
        PermitSurrenderReviewDeterminationGrant reviewDetermination = PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .stopDate(LocalDate.now().minusDays(1))
                .build();
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> handler.validateReview(reviewDecision, reviewDetermination));
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }
}
