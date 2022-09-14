package uk.gov.pmrv.api.migration.ftp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.migration.MigrationConstants;

@Testcontainers
@SpringBootTest
public class FtpFileServiceIT extends AbstractContainerBaseTest {

    @Autowired 
    private FtpFileService ftpFileService;
    
//    @Test
    public void fetchFile() {
        String file = MigrationConstants.FTP_SERVER_FILE_ATTACHMENT_DIRECTORY + "/1121.att";
        FtpFileDTOResult ftpFileDTOResult = ftpFileService.fetchFile(file);
        assertThat(ftpFileDTOResult).isNotNull();
        assertThat(ftpFileDTOResult.getFileDTO()).isNotNull();
        assertThat(ftpFileDTOResult.getFileDTO().getFileName()).isEqualTo("1121.att");
        assertThat(ftpFileDTOResult.getFileDTO().getFileContent()).isNotEmpty();
        assertThat(ftpFileDTOResult.getFileDTO().getFileType()).isNotEmpty();
    }
    
//    @Test
    public void fetchFiles() {
        String file1 = MigrationConstants.FTP_SERVER_FILE_ATTACHMENT_DIRECTORY + "/1121.att";
        String file2 = MigrationConstants.FTP_SERVER_FILE_ATTACHMENT_DIRECTORY + "/1122.att";
        List<FtpFileDTOResult> fileDTOs = ftpFileService.fetchFiles(List.of(file1, file2));
        assertThat(fileDTOs).isNotEmpty();
        assertThat(fileDTOs).extracting(FtpFileDTOResult::getFileDTO).extracting(FileDTO::getFileName).containsExactlyInAnyOrder("1121.att", "1122.att");
        assertThat(fileDTOs).extracting(FtpFileDTOResult::getFileDTO).extracting(FileDTO::getFileContent).isNotEmpty();
        assertThat(fileDTOs).extracting(FtpFileDTOResult::getFileDTO).extracting(FileDTO::getFileType).isNotEmpty();
    }
}
