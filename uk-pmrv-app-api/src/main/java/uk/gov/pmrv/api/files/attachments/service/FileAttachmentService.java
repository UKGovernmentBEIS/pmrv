package uk.gov.pmrv.api.files.attachments.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.pmrv.api.files.attachments.transform.FileAttachmentMapper;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.service.FileValidatorService;
import uk.gov.pmrv.api.files.common.transform.FileMapper;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class FileAttachmentService {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final List<FileValidatorService> fileValidators;
    private static final FileAttachmentMapper fileAttachmentMapper = Mappers.getMapper(FileAttachmentMapper.class);
    private static final FileMapper fileMapper = Mappers.getMapper(FileMapper.class);

    @Transactional
    public String createFileAttachment(@Valid FileDTO fileDTO, FileStatus status,
                                       PmrvUser authUser) throws IOException {

        fileValidators.forEach(validator -> validator.validate(fileDTO));

        FileAttachment attachment = fileAttachmentMapper.toFileAttachment(fileDTO);
        attachment.setUuid(UUID.randomUUID().toString());
        attachment.setStatus(status);
        attachment.setCreatedBy(authUser.getUserId());

        fileAttachmentRepository.save(attachment);

        return attachment.getUuid();
    }

    @Transactional(readOnly = true)
    public FileDTO getFileDTO(String uuid) {
        return fileMapper.toFileDTO(findFileAttachmentByUuid(uuid));
    }

    @Transactional
    public void updateFileAttachmentStatus(String uuid, FileStatus status) {
        FileAttachment fileAttachment = findFileAttachmentByUuid(uuid);
        fileAttachment.setStatus(status);
        fileAttachmentRepository.save(fileAttachment);
    }

    /**
     * Delete the file attachment provided that is in pending status.
     *
     * @param uuid File uuid
     * @return true if deleted, false otherwise
     */
    @Transactional
    public boolean deletePendingFileAttachment(String uuid) {
        FileAttachment fileAttachment = findFileAttachmentByUuid(uuid);
        if(FileStatus.PENDING == fileAttachment.getStatus()) {
            fileAttachmentRepository.delete(fileAttachment);
            return true;
        }else {
            return false;
        }
    }
    
    public boolean fileAttachmentExist(String uuid) {
        return fileAttachmentsExist(Set.of(uuid));
    }

    public boolean fileAttachmentsExist(Set<String> uuids) {
        return uuids.size() == fileAttachmentRepository.countAllByUuidIn(uuids);
    }

    private FileAttachment findFileAttachmentByUuid(String uuid) {
        return fileAttachmentRepository.findByUuid(uuid)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
