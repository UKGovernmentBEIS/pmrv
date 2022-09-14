package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import org.apache.commons.lang3.RandomStringUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationCancelledServiceTest {

    @InjectMocks
    private PermitNotificationCancelledService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {
        final String requestId = RandomStringUtils.random(5);
        final String assignee = "assignee";
        final PermitNotificationRequestPayload payload = PermitNotificationRequestPayload.builder()
                .operatorAssignee(assignee)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.PERMIT_NOTIFICATION)
                .payload(payload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.cancel(requestId);

        // Verify
        verify(requestService, times(1)).addActionToRequest(request, null,
                RequestActionType.PERMIT_NOTIFICATION_APPLICATION_CANCELLED, assignee);
    }
}
