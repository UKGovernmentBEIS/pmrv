package uk.gov.pmrv.api.permit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;

@ExtendWith(MockitoExtension.class)
class PermitAttachmentServiceTest {

    @InjectMocks
    private PermitAttachmentService service;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private FileAttachmentTokenService fileAttachmentTokenService;

    @Test
    void generateGetFileAttachmentToken() throws IOException {
        String permitId = "1";
        UUID attachmentUuid = UUID.randomUUID();

        PermitContainer permitContainer = PermitContainer.builder()
            .permit(Permit.builder().build())
            .permitAttachments(Map.of(
                attachmentUuid, "file1"
            ))
            .build();

        FileToken fileToken = FileToken.builder().token("token").build();


        when(permitQueryService.getPermitContainerById(permitId)).thenReturn(permitContainer);
        when(fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString())).thenReturn(
            fileToken);

        //invoke
        FileToken result = service.generateGetFileAttachmentToken(permitId, attachmentUuid);

        assertThat(result).isEqualTo(fileToken);
        verify(permitQueryService, times(1)).getPermitContainerById(permitId);
        verify(fileAttachmentTokenService, times(1)).generateGetFileAttachmentToken(attachmentUuid.toString());
    }

    @Test
    void getFileAttachment_uuid_not_found_in_permit() throws IOException {
        String permitId = "1";
        UUID attachmentUuid = UUID.randomUUID();

        PermitContainer permitContainer = PermitContainer.builder()
            .permit(Permit.builder().build())
            .permitAttachments(Map.of(
                UUID.randomUUID(), "file1"
            ))
            .build();

        when(permitQueryService.getPermitContainerById(permitId)).thenReturn(permitContainer);

        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.generateGetFileAttachmentToken(permitId, attachmentUuid);
        });
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(permitQueryService, times(1)).getPermitContainerById(permitId);
        verifyNoInteractions(fileAttachmentTokenService);
    }
}
