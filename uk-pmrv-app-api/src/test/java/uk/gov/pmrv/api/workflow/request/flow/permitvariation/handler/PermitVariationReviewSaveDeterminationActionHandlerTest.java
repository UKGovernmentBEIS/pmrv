package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationReviewService;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewSaveDeterminationActionHandlerTest {

	@InjectMocks
    private PermitVariationReviewSaveDeterminationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationReviewService permitVariationReviewService;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    
    @Test
    void getTypes() {
    	assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION);
    }
    
    @Test
    void process_valid() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION;
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final PermitVariationSaveReviewDeterminationRequestTaskActionPayload payload = PermitVariationSaveReviewDeterminationRequestTaskActionPayload.builder()
        		.determination(PermitVariationGrantDetermination.builder().type(DeterminationType.GRANTED).build())
        		.build();
        
        final PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
        		.build();
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId)
        		.payload(requestTaskPayload)
        		.request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
        		.build();
        
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(payload.getDetermination(), requestTaskPayload, RequestType.PERMIT_VARIATION)).thenReturn(true);

        handler.process(requestTaskId, requestTaskActionType, pmrvUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(payload.getDetermination(), requestTaskPayload, RequestType.PERMIT_VARIATION);
        verify(permitVariationReviewService, times(1)).saveDetermination(payload, requestTask);
    }
    
    @Test
    void process_invalid() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION;
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final PermitVariationSaveReviewDeterminationRequestTaskActionPayload payload = PermitVariationSaveReviewDeterminationRequestTaskActionPayload.builder()
        		.determination(PermitVariationGrantDetermination.builder().type(DeterminationType.GRANTED).build())
        		.build();
        
        final PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
        		.build();
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId)
        		.payload(requestTaskPayload)
        		.request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
        		.build();
        
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(payload.getDetermination(), requestTaskPayload, RequestType.PERMIT_VARIATION)).thenReturn(false);

        final BusinessException be = assertThrows(BusinessException.class,
                () -> handler.process(requestTaskId, requestTaskActionType, pmrvUser, payload));
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(payload.getDetermination(), requestTaskPayload, RequestType.PERMIT_VARIATION);
        verify(permitVariationReviewService, never()).saveDetermination(payload, requestTask);
    }
}
