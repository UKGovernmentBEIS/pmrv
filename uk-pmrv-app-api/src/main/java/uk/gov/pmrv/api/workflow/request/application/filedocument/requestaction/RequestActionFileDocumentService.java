package uk.gov.pmrv.api.workflow.request.application.filedocument.requestaction;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTokenService;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;

@Service
@RequiredArgsConstructor
public class RequestActionFileDocumentService {

    private final RequestActionRepository requestActionRepository;
    private final FileDocumentTokenService fileDocumentTokenService;
    
    @Transactional
    public FileToken generateGetFileDocumentToken(Long requestActionId, UUID fileDocumentUuid) {
        RequestAction requestAction = requestActionRepository.findById(requestActionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if(!requestAction.getPayload().getFileDocuments().containsKey(fileDocumentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, fileDocumentUuid);
        }

        return fileDocumentTokenService.generateGetFileDocumentToken(fileDocumentUuid.toString());
    }
}
