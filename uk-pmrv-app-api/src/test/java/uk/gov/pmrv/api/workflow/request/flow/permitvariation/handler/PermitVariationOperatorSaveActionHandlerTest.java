package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationOperatorSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationOperatorSubmitService;

@ExtendWith(MockitoExtension.class)
class PermitVariationOperatorSaveActionHandlerTest {

	@InjectMocks
    private PermitVariationOperatorSaveActionHandler handler;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
    private PermitVariationOperatorSubmitService permitVariationOperatorSubmitService;
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION;
		PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		PermitVariationOperatorSaveApplicationRequestTaskActionPayload payload = PermitVariationOperatorSaveApplicationRequestTaskActionPayload.builder()
				.permit(Permit.builder()
						.abbreviations(Abbreviations.builder().exist(false).build())
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		handler.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(permitVariationOperatorSubmitService, times(1)).savePermitVariation(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(handler.getTypes())
				.containsExactlyInAnyOrder(RequestTaskActionType.PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION);
	}
}
