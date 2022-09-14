package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

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
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceSaveApplicationAmendActionHandlerTest {

    @InjectMocks
    private PermitIssuanceSaveApplicationAmendActionHandler permitIssuanceSaveApplicationAmendActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitIssuanceReviewService permitIssuanceReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PermitIssuanceSaveApplicationAmendRequestTaskActionPayload permitAmendRequestTaskActionPayload =
            PermitIssuanceSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD)
                .permit(Permit.builder().
                    environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(false).build())
                    .build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        permitIssuanceSaveApplicationAmendActionHandler
            .process(requestTask.getId(), RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND, pmrvUser, permitAmendRequestTaskActionPayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitIssuanceReviewService, times(1)).amendPermit(permitAmendRequestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(permitIssuanceSaveApplicationAmendActionHandler.getTypes())
            .containsExactly(RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND);
    }
}