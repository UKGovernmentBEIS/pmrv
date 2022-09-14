package uk.gov.pmrv.api.migration.files;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileAttachmentMigrationError {

    private String fileAttachmentUuid;
    private String fileName;
    private byte[] fileContent;
    private FileDTO fileDTO;
    private String errorReport;
    
}
