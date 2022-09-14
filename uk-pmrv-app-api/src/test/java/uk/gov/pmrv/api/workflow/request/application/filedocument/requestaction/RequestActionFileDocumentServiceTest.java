package uk.gov.pmrv.api.workflow.request.application.filedocument.requestaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTokenService;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class RequestActionFileDocumentServiceTest {

    @InjectMocks
    private RequestActionFileDocumentService service;

    @Mock
    private RequestActionRepository requestActionRepository;

    @Mock
    private FileDocumentTokenService fileDocumentTokenService;

    @Test
    void generateGetFileDocumentToken() {
        Long requestActionId = 1L;
        UUID fileDocumentUuid = UUID.randomUUID();
        RfiSubmittedRequestActionPayload actionPayload = RfiSubmittedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.RFI_SUBMITTED_PAYLOAD)
            .officialDocument(FileInfoDTO.builder()
                    .name("offdoc")
                    .uuid(fileDocumentUuid.toString())
                    .build())
            .build();
        RequestAction requestAction = RequestAction.builder().id(requestActionId).payload(actionPayload).build();
        
        FileToken fileToken = FileToken.builder().token("token").build();

        when(requestActionRepository.findById(requestActionId)).thenReturn(Optional.of(requestAction));
        when(fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString()))
            .thenReturn(fileToken);

        FileToken result = service.generateGetFileDocumentToken(requestActionId, fileDocumentUuid);

        assertThat(result).isEqualTo(fileToken);
        
        verify(requestActionRepository, times(1)).findById(requestActionId);

    }

    @Test
    void generateGetFileDocumentToken_request_action_not_exists(){
        Long requestActionId = 1L;
        UUID fileDocumentUuid = UUID.randomUUID();

        when(requestActionRepository.findById(requestActionId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () ->
        service.generateGetFileDocumentToken(requestActionId, fileDocumentUuid));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);

        verify(requestActionRepository, times(1)).findById(requestActionId);
        verifyNoInteractions(fileDocumentTokenService);
    }

    @Test
    void generateGetFileDocumentToken_filedocument_not_exists_in_payload() {
        Long requestActionId = 1L;
        UUID fileDocumentUuid = UUID.randomUUID();
        RfiSubmittedRequestActionPayload actionPayload = RfiSubmittedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.RFI_SUBMITTED_PAYLOAD)
                .officialDocument(FileInfoDTO.builder()
                        .name("offdoc")
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();
        RequestAction requestAction = RequestAction.builder().id(requestActionId).payload(actionPayload).build();

        when(requestActionRepository.findById(requestActionId)).thenReturn(Optional.of(requestAction));

        BusinessException businessException = assertThrows(BusinessException.class, () ->
        service.generateGetFileDocumentToken(requestActionId, fileDocumentUuid));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);

        verify(requestActionRepository, times(1)).findById(requestActionId);
        verifyNoInteractions(fileDocumentTokenService);
    }
}
