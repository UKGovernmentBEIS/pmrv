package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderApplicationRejectedOperatorMapperTest {

    @InjectMocks
    private PermitSurrenderApplicationRejectedOperatorMapper mapper;

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.PERMIT_SURRENDER_APPLICATION_REJECTED)
                .payload(PermitSurrenderApplicationRejectedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD)
                        .reviewDetermination(PermitSurrenderReviewDeterminationReject.builder()
                                .reason("reason")
                                .build())
                        .build())
                .build();
        
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        PermitSurrenderApplicationRejectedRequestActionPayload resultPayload = (PermitSurrenderApplicationRejectedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewDetermination().getReason()).isNull();
    }
    
    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_SURRENDER_APPLICATION_REJECTED);
    }
    
    @Test
    void getUserRoleType() {
        assertThat(mapper.getUserRoleType()).isEqualTo(RoleType.OPERATOR);
    }
}
