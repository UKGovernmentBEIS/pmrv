package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.handler;

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
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.service.RequestPermitApplyService;

@ExtendWith(MockitoExtension.class)
class PermitApplySaveActionHandlerTest {

    @InjectMocks
    private PermitApplySaveActionHandler handler;

    @Mock
    private RequestPermitApplyService requestPermitApplyService;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void doProcess() {
        //prepare
        PermitIssuanceSaveApplicationRequestTaskActionPayload permitApplySavePayload =
            PermitIssuanceSaveApplicationRequestTaskActionPayload.builder().payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD)
                    .permit(Permit.builder().
                            environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(false).build())
                            .build())
                    .build();
        
        RequestTask requestTask = RequestTask.builder().id(1L).build();
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION, pmrvUser, permitApplySavePayload);
        
        //verify
        verify(requestPermitApplyService, times(1)).applySaveAction(permitApplySavePayload, requestTask);
    }
}
