package uk.gov.pmrv.api.files.attachments.repository;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.repository.FileEntityRepository;

@Repository
public interface FileAttachmentRepository extends FileEntityRepository<FileAttachment, Long> {
    
    @Transactional(readOnly = true)
    List<FileAttachment> findByStatus(FileStatus status);
    
    @Transactional(readOnly = true)
    long countAllByUuidIn(Set<String> uuids);
}
