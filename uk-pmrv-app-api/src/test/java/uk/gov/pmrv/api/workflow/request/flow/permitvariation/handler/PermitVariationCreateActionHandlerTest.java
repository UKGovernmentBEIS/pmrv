package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationCreateActionHandlerTest {

	@InjectMocks
    private PermitVariationCreateActionHandler handler;
	
	@Mock
    private StartProcessRequestService startProcessRequestService;
	
	@Test
	void process_regulator() {
		Long accountId = 1L;
		RequestCreateActionType type = RequestCreateActionType.PERMIT_VARIATION;
		RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        PmrvUser pmrvUser = PmrvUser.builder()
        		.roleType(RoleType.REGULATOR)
        		.userId("userId")
        		.build();
        
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_VARIATION)
                .accountId(accountId)
                .requestPayload(PermitVariationRequestPayload.builder()
            	        .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
            	        .regulatorAssignee(pmrvUser.getUserId())
            	        .build())
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, RoleType.REGULATOR.name()
                		))
				.requestMetadata(PermitVariationRequestMetadata.builder()
						.type(RequestMetadataType.PERMIT_VARIATION)
						.build())
                .build();
        
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        
        String result = handler.process(accountId, type, payload, pmrvUser);
        
        assertThat(result).isEqualTo("1");
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
	}
	
	@Test
	void process_operator() {
		Long accountId = 1L;
		RequestCreateActionType type = RequestCreateActionType.PERMIT_VARIATION;
		RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        PmrvUser pmrvUser = PmrvUser.builder()
        		.roleType(RoleType.OPERATOR)
        		.userId("userId")
        		.build();
        
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_VARIATION)
                .accountId(accountId)
                .requestPayload(PermitVariationRequestPayload.builder()
            	        .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
            	        .operatorAssignee(pmrvUser.getUserId())
            	        .build())
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, RoleType.OPERATOR.name()
                		))
				.requestMetadata(PermitVariationRequestMetadata.builder()
						.type(RequestMetadataType.PERMIT_VARIATION)
						.build())
                .build();
        
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        
        String result = handler.process(accountId, type, payload, pmrvUser);
        
        assertThat(result).isEqualTo("1");
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
	}
	
	@Test
	void getType() {
		assertThat(handler.getType()).isEqualTo(RequestCreateActionType.PERMIT_VARIATION);
	}
}
