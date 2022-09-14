package uk.gov.pmrv.api.workflow.request.application.attachment.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class RequestTaskAttachmentServiceTest {

    @InjectMocks
    private RequestTaskAttachmentService requestTaskAttachmentService;

    @Mock
    private FileAttachmentTokenService fileAttachmentTokenService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void generateGetFileAttachmentToken() {
        Long requestTaskId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssueRequestTaskPayload =
            PermitIssuanceApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
            .permitAttachments(Map.of(attachmentUuid, "attachmentName"))
            .build();
        requestTask.setPayload(permitIssueRequestTaskPayload);

        FileToken fileToken = FileToken.builder().token("token").tokenExpirationMinutes(5L).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString()))
            .thenReturn(fileToken);

        FileToken result = requestTaskAttachmentService.generateGetFileAttachmentToken(requestTaskId, attachmentUuid);

        assertThat(result).isEqualTo(fileToken);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(fileAttachmentTokenService, times(1)).generateGetFileAttachmentToken(attachmentUuid.toString());
    }

    @Test
    void generateGetFileAttachmentToken_attachment_not_in_payload() {
        Long requestTaskId = 1L;
        UUID attachmentUuid1 = UUID.randomUUID();
        UUID attachmentUuid2 = UUID.randomUUID();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssueRequestTaskPayload =
            PermitIssuanceApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
                .permitAttachments(Map.of(attachmentUuid1, "attachmentName"))
                .build();
        requestTask.setPayload(permitIssueRequestTaskPayload);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
            requestTaskAttachmentService.generateGetFileAttachmentToken(requestTaskId, attachmentUuid2));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoInteractions(fileAttachmentTokenService);
    }
}