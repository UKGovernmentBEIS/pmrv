package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationDeemWithdraw;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderApplicationDeemedWithdrawnOperatorMapperTest {

    @InjectMocks
    private PermitSurrenderApplicationDeemedWithdrawnOperatorMapper mapper;

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN)
                .payload(PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)
                        .reviewDetermination(PermitSurrenderReviewDeterminationDeemWithdraw.builder()
                                .reason("reason")
                                .build())
                        .build())
                .build();
        
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload resultPayload = (PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewDetermination().getReason()).isNull();
    }
    
    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN);
    }
    
    @Test
    void getUserRoleType() {
        assertThat(mapper.getUserRoleType()).isEqualTo(RoleType.OPERATOR);
    }
}
