package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class PermitRevokedServiceTest {

    @InjectMocks
    private PermitRevokedService service;

    @Mock
    private AccountStatusService accountStatusService;

    @Mock
    private RequestService requestService;

    @Test
    void applySavePayload() {

        final Request request = Request.builder()
            .id("id")
            .type(RequestType.PERMIT_REVOCATION)
            .accountId(1L)
            .build();

        when(requestService.findRequestById("id")).thenReturn(request);

        service.executePermitRevokedPostActions("id");

        verify(accountStatusService, times(1)).handlePermitRevoked(1L);
    }
}