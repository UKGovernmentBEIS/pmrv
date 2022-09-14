package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessation;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;

class PermitSurrenderCessationCompletedOperatorMapperTest {

    private final PermitSurrenderCessationCompletedOperatorMapper cessationCompletedOperatorMapper =
        new PermitSurrenderCessationCompletedOperatorMapper();

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.PERMIT_SURRENDER_CESSATION_COMPLETED)
            .payload(PermitCessationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD)
                .cessation(PermitCessation.builder().notes("notes").build())
                .build())
            .build();

        RequestActionDTO resultDTO = cessationCompletedOperatorMapper.toRequestActionDTO(requestAction);

        assertEquals(requestAction.getType(), resultDTO.getType());
        PermitCessationCompletedRequestActionPayload resultPayload =
            (PermitCessationCompletedRequestActionPayload) resultDTO.getPayload();
        assertNotNull(resultPayload.getCessation());
        assertNull(resultPayload.getCessation().getNotes());
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.PERMIT_SURRENDER_CESSATION_COMPLETED, cessationCompletedOperatorMapper.getRequestActionType());
    }

    @Test
    void getUserRoleType() {
        assertEquals(RoleType.OPERATOR, cessationCompletedOperatorMapper.getUserRoleType());
    }
}