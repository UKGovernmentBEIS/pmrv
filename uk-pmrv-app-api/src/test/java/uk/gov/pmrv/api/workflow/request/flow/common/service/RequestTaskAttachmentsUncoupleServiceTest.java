package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class RequestTaskAttachmentsUncoupleServiceTest {
    
    @InjectMocks
    private RequestTaskAttachmentsUncoupleService service;
    
    @Mock
    private FileAttachmentService fileAttachmentService;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void cleanUpAttachmentsOnTaskCompletion() {
        UUID attachmentUuid = UUID.randomUUID();
        UUID unreferencedAttachmentUuid = UUID.randomUUID();
        Map<UUID, String> permitAttachments = new HashMap<>();
        permitAttachments.put(attachmentUuid, "file1");
        permitAttachments.put(unreferencedAttachmentUuid, "unreference_file");
        PermitIssuanceApplicationSubmitRequestTaskPayload permitApplyPayload = PermitIssuanceApplicationSubmitRequestTaskPayload
            .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
                .permit(Permit.builder()
                    .managementProcedures(ManagementProcedures.builder()
                        .monitoringReporting(MonitoringReporting.builder().organisationCharts(Set.of(attachmentUuid)).build())
                        .build())
                    .build())
                .permitAttachments(permitAttachments)
                .build();

        RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT)
            .payload(permitApplyPayload)
            .build();
        
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        service.uncoupleAttachments(1L);
        
        verify(fileAttachmentService, times(1)).updateFileAttachmentStatus(attachmentUuid.toString(), FileStatus.SUBMITTED);
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(unreferencedAttachmentUuid.toString());
        verify(fileAttachmentService, never()).deletePendingFileAttachment(attachmentUuid.toString());
        
        assertThat(requestTask.getPayload()).isInstanceOf(PermitIssuanceApplicationSubmitRequestTaskPayload.class);
        PermitIssuanceApplicationSubmitRequestTaskPayload
            payloadSaved = (PermitIssuanceApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getPermitAttachments()).containsOnlyKeys(attachmentUuid);
        
    }

    @Test
    void cleanUpAttachmentsOnTaskCompletionForReview() {

        final UUID referencedPermit = UUID.randomUUID();
        final UUID unreferencedPermit = UUID.randomUUID();
        
        final UUID referencedReview = UUID.randomUUID();
        final UUID unreferencedReview = UUID.randomUUID();

        final UUID rfi1 = UUID.randomUUID();
        final UUID rfi2 = UUID.randomUUID();

        final Map<UUID, String> permitAttachments = new HashMap<>();
        permitAttachments.put(referencedPermit, "referenced-permit");
        permitAttachments.put(unreferencedPermit, "unreferenced-permit");

        final Map<UUID, String> reviewAttachments = new HashMap<>();
        reviewAttachments.put(referencedReview, "referenced-review");
        reviewAttachments.put(unreferencedReview, "unreferenced-review");

        final Map<UUID, String> rfiAttachments = new HashMap<>();
        rfiAttachments.put(rfi1, "rfi1");
        rfiAttachments.put(rfi2, "rfi2");

        final PermitIssuanceReviewDecision decision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.REJECTED)
            .requiredChange(new PermitReviewDecisionRequiredChange("changesRequired", Set.of(referencedReview)))
            .notes("notes")
            .build();
        
        final PermitIssuanceApplicationReviewRequestTaskPayload payload = PermitIssuanceApplicationReviewRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
            .permit(Permit.builder()
                .managementProcedures(ManagementProcedures.builder()
                    .monitoringReporting(MonitoringReporting.builder().organisationCharts(Set.of(referencedPermit)).build())
                    .build())
                .build())
            .reviewGroupDecisions(Map.of(PermitReviewGroup.INSTALLATION_DETAILS, decision))
            .permitAttachments(permitAttachments)
            .reviewAttachments(reviewAttachments)
            .rfiAttachments(rfiAttachments)
            .build();

        RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT)
            .payload(payload)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(fileAttachmentService.deletePendingFileAttachment(unreferencedPermit.toString())).thenReturn(true);
        when(fileAttachmentService.deletePendingFileAttachment(unreferencedReview.toString())).thenReturn(true);
        when(fileAttachmentService.deletePendingFileAttachment(rfi1.toString())).thenReturn(true);
        when(fileAttachmentService.deletePendingFileAttachment(rfi2.toString())).thenReturn(true);

        service.uncoupleAttachments(1L);

        verify(fileAttachmentService, times(1)).updateFileAttachmentStatus(referencedPermit.toString(), FileStatus.SUBMITTED);
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(unreferencedPermit.toString());
        verify(fileAttachmentService, never()).deletePendingFileAttachment(referencedPermit.toString());
        
        verify(fileAttachmentService, times(1)).updateFileAttachmentStatus(referencedReview.toString(), FileStatus.SUBMITTED);
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(unreferencedReview.toString());
        verify(fileAttachmentService, never()).deletePendingFileAttachment(referencedReview.toString());
        
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(rfi1.toString());
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(rfi2.toString());

        assertThat(requestTask.getPayload()).isInstanceOf(PermitIssuanceApplicationReviewRequestTaskPayload.class);
        PermitIssuanceApplicationReviewRequestTaskPayload
            payloadSaved = (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getPermitAttachments()).containsOnlyKeys(referencedPermit);
        assertThat(payloadSaved.getReviewAttachments()).containsOnlyKeys(referencedReview);
        assertThat(payloadSaved.getRfiAttachments()).isEmpty();
    }

    @Test
    void deletePendingAttachments() {

        final UUID referencedPermit = UUID.randomUUID();
        final UUID unreferencedPermit = UUID.randomUUID();

        final UUID referencedReview = UUID.randomUUID();
        final UUID unreferencedReview = UUID.randomUUID();

        final Map<UUID, String> permitAttachments = new HashMap<>();
        permitAttachments.put(referencedPermit, "referenced-permit");
        permitAttachments.put(unreferencedPermit, "unreferenced-permit");

        final Map<UUID, String> reviewAttachments = new HashMap<>();
        reviewAttachments.put(referencedReview, "referenced-review");
        reviewAttachments.put(unreferencedReview, "unreferenced-review");

        final PermitIssuanceApplicationAmendsSubmitRequestTaskPayload payload = 
            PermitIssuanceApplicationAmendsSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
            .permitAttachments(permitAttachments)
            .reviewAttachments(reviewAttachments)
            .build();

        RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT)
            .payload(payload)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(fileAttachmentService.deletePendingFileAttachment(unreferencedPermit.toString())).thenReturn(true);
        when(fileAttachmentService.deletePendingFileAttachment(unreferencedReview.toString())).thenReturn(true);
        when(fileAttachmentService.deletePendingFileAttachment(referencedPermit.toString())).thenReturn(false);
        when(fileAttachmentService.deletePendingFileAttachment(referencedReview.toString())).thenReturn(false);

        service.deletePendingAttachments(1L);

        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(unreferencedPermit.toString());
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(unreferencedReview.toString());
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(referencedPermit.toString());
        verify(fileAttachmentService, times(1)).deletePendingFileAttachment(referencedReview.toString());

        
        assertThat(requestTask.getPayload()).isInstanceOf(PermitIssuanceApplicationAmendsSubmitRequestTaskPayload.class);
        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload
            payloadSaved = (PermitIssuanceApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getPermitAttachments()).containsOnlyKeys(referencedPermit);
        assertThat(payloadSaved.getReviewAttachments()).containsOnlyKeys(referencedReview);

    }

}
