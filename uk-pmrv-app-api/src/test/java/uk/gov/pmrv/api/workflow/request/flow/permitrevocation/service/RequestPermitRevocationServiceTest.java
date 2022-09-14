package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class RequestPermitRevocationServiceTest {

    @InjectMocks
    private RequestPermitRevocationService service;


    @Test
    void applySavePayload() {

        final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload =
            PermitRevocationApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD)
                .permitRevocation(PermitRevocation.builder()
                    .reason("the reason")
                    .build())
                .sectionsCompleted(Map.of("reason", false))
                .build();

        final PermitRevocationSaveApplicationRequestTaskActionPayload actionPayload =
            PermitRevocationSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD)
                .permitRevocation(
                    PermitRevocation.builder()
                        .reason("some other reason")
                        .annualEmissionsReportDate(LocalDate.of(2022, 1, 1))
                        .build())
                .sectionsCompleted(Map.of("reason", true))
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .type(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT)
            .payload(taskPayload)
            .build();

        service.applySavePayload(actionPayload, requestTask);

        assertThat(taskPayload.getPermitRevocation()).isEqualTo(actionPayload.getPermitRevocation());
        assertThat(taskPayload.getSectionsCompleted()).isEqualTo(actionPayload.getSectionsCompleted());
    }

    @Test
    void requestPeerReview() {

        final PermitRevocation permitRevocation = PermitRevocation.builder()
            .reason("reason")
            .effectiveDate(LocalDate.of(2024, 1, 2))
            .build();
        final Request request = Request.builder()
            .payload(PermitRevocationRequestPayload.builder().build())
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .type(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT)
            .payload(PermitRevocationApplicationSubmitRequestTaskPayload.builder()
                .permitRevocation(permitRevocation)
                .sectionsCompleted(Map.of("abc", true))
                .build())
            .request(request)
            .build();

        final String selectedPeerReviewer = "selectedPeerReviewer";
        final String regulatorReviewer = "regulatorReviewer";

        service.requestPeerReview(requestTask, selectedPeerReviewer, regulatorReviewer);

        assertThat(((PermitRevocationRequestPayload) request.getPayload()).getPermitRevocation()).isEqualTo(permitRevocation);
        assertThat(((PermitRevocationRequestPayload) request.getPayload()).getSectionsCompleted()).isEqualTo(Map.of("abc", true));
        assertThat(request.getPayload().getRegulatorReviewer()).isEqualTo("regulatorReviewer");
        assertThat(request.getPayload().getRegulatorPeerReviewer()).isEqualTo("selectedPeerReviewer");
    }
    
    @Test
    void applyWithdrawPayload() {
    	final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
    			.build();
        final PermitRevocationWaitForAppealRequestTaskPayload taskPayload =
            PermitRevocationWaitForAppealRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD)
                .build();

        final UUID file = UUID.randomUUID();
        final PermitRevocationApplicationWithdrawRequestTaskActionPayload actionPayload =
            PermitRevocationApplicationWithdrawRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_REVOCATION_WITHDRAW_APPLICATION_PAYLOAD)
                .reason("the reason")
                .files(Set.of(file))
                .build();

        final Request request = Request.builder()
        		.type(RequestType.PERMIT_REVOCATION)
        		.payload(requestPayload)
        		.build();
        final RequestTask requestTask = RequestTask.builder()
        	.request(request)
            .type(RequestTaskType.PERMIT_REVOCATION_WAIT_FOR_APPEAL)
            .payload(taskPayload)
            .build();

        assertThat(requestPayload.getWithdrawCompletedDate()).isNull();
        
        service.applyWithdrawPayload(actionPayload, requestTask);

        assertThat(taskPayload.getReason()).isEqualTo("the reason");
        assertThat(taskPayload.getWithdrawFiles()).isEqualTo(Set.of(file));
        assertThat(requestPayload.getWithdrawCompletedDate()).isNotNull();
    }
}
