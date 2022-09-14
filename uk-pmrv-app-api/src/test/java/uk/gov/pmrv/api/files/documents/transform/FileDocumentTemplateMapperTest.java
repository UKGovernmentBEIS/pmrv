package uk.gov.pmrv.api.files.documents.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.domain.FileDocumentTemplate;

class FileDocumentTemplateMapperTest {

    private FileDocumentTemplateMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(FileDocumentTemplateMapper.class);
    }
    
    @Test
    void toFileDTO() {
        String uuid = UUID.randomUUID().toString();
        String name = "file document name";
        byte[] content = "cotnent".getBytes();
        FileDocumentTemplate fileDocumentTemplate = FileDocumentTemplate.builder()
            .uuid(uuid)
            .fileName(name)
            .fileContent(content)
            .fileSize(content.length)
            .fileType("docx")
            .status(FileStatus.PENDING)
            .createdBy("user")
            .lastUpdatedOn(LocalDateTime.now())
            .build();

        FileDTO result = mapper.toFileDTO(fileDocumentTemplate);

        assertThat(result.getFileName()).isEqualTo(name);
        assertThat(result.getFileContent()).isEqualTo(content);
        assertThat(result.getFileType()).isEqualTo("docx");
        assertThat(result.getFileSize()).isEqualTo(content.length);
    }

    @Test
    void toFileInfoDTO() {
        String uuid = UUID.randomUUID().toString();
        String name = "file document name";
        FileDocumentTemplate fileDocumentTemplate = FileDocumentTemplate.builder()
            .uuid(uuid)
            .fileName(name)
            .fileContent(name.getBytes())
            .fileSize(name.length())
            .fileType("docx")
            .status(FileStatus.PENDING)
            .createdBy("user")
            .lastUpdatedOn(LocalDateTime.now())
            .build();

        FileInfoDTO result = mapper.toFileInfoDTO(fileDocumentTemplate);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
    }

    @Test
    void toFileDocumentTemplate() {
        FileStatus fileStatus = FileStatus.PENDING;
        String userId = "userId";
        byte[] content = "content".getBytes();
        FileDTO fileDTO = FileDTO.builder()
            .fileName("name")
            .fileSize(10)
            .fileType("application/pdf")
            .fileContent(content)
            .build();

        FileDocumentTemplate fileDocumentTemplate = mapper.toFileDocumentTemplate(fileDTO, fileStatus, userId);

        assertNotNull(fileDocumentTemplate);
        assertNotNull(fileDocumentTemplate.getUuid());
        assertEquals(fileDTO.getFileName(), fileDocumentTemplate.getFileName());
        assertThat(fileDocumentTemplate.getFileContent()).isNotEmpty();
        assertEquals(fileDTO.getFileType(), fileDocumentTemplate.getFileType());
        assertEquals(fileDTO.getFileSize(), fileDocumentTemplate.getFileSize());
        assertEquals(fileStatus, fileDocumentTemplate.getStatus());
        assertEquals(userId, fileDocumentTemplate.getCreatedBy());
    }
}