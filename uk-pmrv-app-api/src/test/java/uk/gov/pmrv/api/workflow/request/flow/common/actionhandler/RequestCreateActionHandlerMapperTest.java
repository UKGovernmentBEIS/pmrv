package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.validation.EnabledWorkflowValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler.PermitSurrenderCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler.PermitVariationCreateActionHandler;

@ExtendWith(MockitoExtension.class)
public class RequestCreateActionHandlerMapperTest {

    @Mock
    private EnabledWorkflowValidator enabledWorkflowValidator;

    @Mock
    private PermitSurrenderCreateActionHandler permitSurrenderCreateActionHandler;

    @Mock
    private PermitVariationCreateActionHandler permitVariationCreateActionHandler;

    @Test
    public void get_not_return_disabled_workflows() {
        List<RequestCreateActionHandler<? extends RequestCreateActionPayload>> handlers = List.of(permitSurrenderCreateActionHandler,
            permitVariationCreateActionHandler);

        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_VARIATION)).thenReturn(true);
        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_SURRENDER)).thenReturn(false);
        when(permitSurrenderCreateActionHandler.getType()).thenReturn(RequestCreateActionType.PERMIT_SURRENDER);
        when(permitVariationCreateActionHandler.getType()).thenReturn(RequestCreateActionType.PERMIT_VARIATION);
        RequestCreateActionHandlerMapper requestCreateActionHandlerMapper = new RequestCreateActionHandlerMapper(handlers, enabledWorkflowValidator);

        var handler = requestCreateActionHandlerMapper.get(RequestCreateActionType.PERMIT_VARIATION);

        assertThat(handler).isEqualTo(permitVariationCreateActionHandler);

        assertThrows(BusinessException.class, () -> requestCreateActionHandlerMapper.get(RequestCreateActionType.PERMIT_SURRENDER));
    }
}
