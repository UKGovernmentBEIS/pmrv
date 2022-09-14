package uk.gov.pmrv.api.files.documents.repository;

import org.springframework.stereotype.Repository;

import uk.gov.pmrv.api.files.common.repository.FileEntityRepository;
import uk.gov.pmrv.api.files.documents.domain.FileDocumentTemplate;

@Repository
public interface FileDocumentTemplateRepository extends FileEntityRepository<FileDocumentTemplate, Long> {

    boolean existsByUuid(String uuid);
}
