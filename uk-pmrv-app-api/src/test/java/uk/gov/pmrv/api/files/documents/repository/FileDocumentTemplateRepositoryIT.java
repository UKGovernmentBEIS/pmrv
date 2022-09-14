package uk.gov.pmrv.api.files.documents.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.documents.domain.FileDocumentTemplate;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class FileDocumentTemplateRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FileDocumentTemplateRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void existsByUuid() {
        String uuid = UUID.randomUUID().toString();
        FileDocumentTemplate fileDocumentTemplate =
                createFileDocumentTemplate("file.docx", uuid, FileStatus.SUBMITTED);

        entityManager.persist(fileDocumentTemplate);
        flushAndClear();

        assertTrue(repo.existsByUuid(uuid));
    }

    @Test
    void existsByDocumentTemplateIdAndUuid_false() {
        String uuid = UUID.randomUUID().toString();
        FileDocumentTemplate fileDocumentTemplate =
                createFileDocumentTemplate("file.docx", uuid, FileStatus.SUBMITTED);

        entityManager.persist(fileDocumentTemplate);
        flushAndClear();

        assertFalse(repo.existsByUuid(UUID.randomUUID().toString()));
    }

    private FileDocumentTemplate createFileDocumentTemplate(String filename, String uuid, FileStatus status) {
        return FileDocumentTemplate.builder()
            .uuid(uuid)
            .fileName(filename)
            .fileContent(filename.getBytes())
            .fileSize(filename.length())
            .fileType("docx")
            .status(status)
            .createdBy("user")
            .lastUpdatedOn(LocalDateTime.now())
            .build();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}