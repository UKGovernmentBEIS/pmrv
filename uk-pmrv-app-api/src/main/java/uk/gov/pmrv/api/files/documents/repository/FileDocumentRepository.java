package uk.gov.pmrv.api.files.documents.repository;

import org.springframework.stereotype.Repository;

import uk.gov.pmrv.api.files.common.repository.FileEntityRepository;
import uk.gov.pmrv.api.files.documents.domain.FileDocument;

@Repository
public interface FileDocumentRepository extends FileEntityRepository<FileDocument, Long> {

    boolean existsByUuid(String uuid);
}
