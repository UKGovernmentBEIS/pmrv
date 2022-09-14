package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderCreateActionHandlerTest {

    @InjectMocks
    private PermitSurrenderCreateActionHandler handler;
    
    @Mock
    private StartProcessRequestService startProcessRequestService;
    
    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(RequestCreateActionType.PERMIT_SURRENDER);
    }
    
    @Test
    void process() {
        Long accountId = 1L;
        RequestCreateActionType type = RequestCreateActionType.PERMIT_SURRENDER;
        RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD).build();
        PmrvUser pmrvUser = PmrvUser.builder().userId("user").authorities(List.of(PmrvAuthority.builder()
                .accountId(accountId).build()))
                .build();

        RequestParams expectedRequestParams = RequestParams.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .accountId(accountId)
            .requestPayload(PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .operatorAssignee(pmrvUser.getUserId())
                .build())
            .requestMetadata(PermitSurrenderRequestMetadata.builder()
                    .type(RequestMetadataType.PERMIT_SURRENDER)
                    .build())
            .build();
        
        when(startProcessRequestService.startProcess(expectedRequestParams)).thenReturn(Request.builder().id("requestId").build());

        final String requestId = handler.process(accountId, type, payload, pmrvUser);

        verify(startProcessRequestService, times(1)).startProcess(expectedRequestParams);
        
        assertEquals("requestId", requestId);
    }
}
