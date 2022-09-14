package uk.gov.pmrv.api.migration.files;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.pmrv.api.files.common.FileConstants;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.service.FileScanService;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.ftp.FtpFileDTOResult;
import uk.gov.pmrv.api.migration.ftp.FtpFileService;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FileAttachmentMigrationService {
    
    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileScanService fileScanService;
    private final FtpFileService ftpService;
    
    @Transactional
    public Optional<FileAttachmentMigrationError> migrateFileAttachment(String fileAttachmentUuid) {
        // 1. fetch existing (temp) file attachment from PMRV DB
        Optional<FileAttachment> fileAttachmentOpt = fileAttachmentRepository.findByUuid(fileAttachmentUuid);
        if(fileAttachmentOpt.isEmpty()) {
            return Optional.of(FileAttachmentMigrationError.builder().fileAttachmentUuid(fileAttachmentUuid)
                    .errorReport("File attachment not found in the PMRV DB")
                    .build());
        }
        FileAttachment fileAttachment = fileAttachmentOpt.get();
        if(fileAttachment.getStatus() != FileStatus.PENDING_MIGRATION) {
            return Optional.of(FileAttachmentMigrationError.builder()
                    .fileAttachmentUuid(fileAttachmentUuid)
                    .fileName(fileAttachment.getFileName())
                    .fileContent(fileAttachment.getFileContent())
                    .errorReport("File attachment is not in pending_migration status")
                    .build());
        }
        
        // 2. Fetch actual file from ETS FTP server
        final String ftpServertDirectory = MigrationConstants.FTP_SERVER_FILE_ATTACHMENT_DIRECTORY;
        final String fileStoredName = new String(fileAttachment.getFileContent(), StandardCharsets.UTF_8);
        final String filePath = ftpServertDirectory + "/" + fileStoredName;
        FtpFileDTOResult etsFtpFileDTOResult = ftpService.fetchFile(filePath);
        if(etsFtpFileDTOResult.getErrorReport() != null) {
            return Optional.of(FileAttachmentMigrationError.builder()
                    .fileAttachmentUuid(fileAttachmentUuid)
                    .fileName(fileAttachment.getFileName())
                    .fileContent(fileAttachment.getFileContent())
                    .fileDTO(etsFtpFileDTOResult.getFileDTO())
                    .errorReport(etsFtpFileDTOResult.getErrorReport()).build());
        }
        
        FileDTO etsFileDTO = etsFtpFileDTOResult.getFileDTO();
        
        // 3. Validate file
        try {
            validateFileDTO(etsFileDTO);
        }catch (Exception e) {
            return Optional.of(FileAttachmentMigrationError.builder()
                    .fileAttachmentUuid(fileAttachmentUuid)
                    .fileName(fileAttachment.getFileName())
                    .fileContent(fileAttachment.getFileContent())
                    .errorReport(e.getMessage())
                    .build());
        }
        
        // 4. update file attachment with actual values fetched from ETS FTP server
        fileAttachment.setFileContent(etsFileDTO.getFileContent());
        fileAttachment.setFileSize(etsFileDTO.getFileSize());
        fileAttachment.setFileType(etsFileDTO.getFileType());
        fileAttachment.setStatus(FileStatus.SUBMITTED);
        fileAttachmentRepository.save(fileAttachment);
        
        return Optional.empty();
    }
    
    @Transactional
    public List<FileAttachmentMigrationError> migrateFileAttachments(List<String> fileAttachmentUuids) {
        List<FileAttachmentMigrationError> results = new ArrayList<>();
        
        List<FileAttachment> fileAttachmentsToMigrate = new ArrayList<>();
        
        // 1. fetch existing (temp) file attachment from PMRV DB
        for(String fileAttachmentUuid : fileAttachmentUuids) {
            Optional<FileAttachment> fileAttachmentOpt = fileAttachmentRepository.findByUuid(fileAttachmentUuid);
            if(fileAttachmentOpt.isEmpty()) {
                results.add(FileAttachmentMigrationError.builder()
                            .fileAttachmentUuid(fileAttachmentUuid)
                            .errorReport("File attachment not found in the PMRV DB")
                            .build());
            }else {
                FileAttachment fileAttachment = fileAttachmentOpt.get();
                if(fileAttachment.getStatus() != FileStatus.PENDING_MIGRATION) {
                    results.add(FileAttachmentMigrationError.builder()
                                .fileAttachmentUuid(fileAttachmentUuid)
                                .fileName(fileAttachment.getFileName())
                                .fileContent(fileAttachment.getFileContent())
                                .errorReport("File attachment is not in pending_migration status")
                                .build());
                } else {
                    fileAttachmentsToMigrate.add(fileAttachmentOpt.get());
                }
            }
        }
        
        // 2. Fetch actual file from ETS FTP server
        final String ftpServertDirectory = MigrationConstants.FTP_SERVER_FILE_ATTACHMENT_DIRECTORY;
        List<String> fileAttachmentPaths = fileAttachmentsToMigrate.stream()
                .map(fileAttachment -> new String(fileAttachment.getFileContent(), StandardCharsets.UTF_8))
                .map(fileStoredName -> ftpServertDirectory + "/" + fileStoredName)
                .collect(Collectors.toList());
        List<FtpFileDTOResult> ftpFileDTOResultResults = ftpService.fetchFiles(fileAttachmentPaths);
        
        
        for(FtpFileDTOResult ftpFileDTOResult: ftpFileDTOResultResults) {
            FileAttachment attachment = fileAttachmentsToMigrate.stream()
                    .filter(att -> (new String(att.getFileContent(), StandardCharsets.UTF_8)).equals(ftpFileDTOResult.getFileDTO().getFileName())).findFirst().get();
            if(ftpFileDTOResult.getErrorReport() != null) {
                results.add(FileAttachmentMigrationError.builder()
                            .fileAttachmentUuid(attachment.getUuid())
                            .fileName(attachment.getFileName())
                            .fileContent(attachment.getFileContent())
                            .errorReport(ftpFileDTOResult.getErrorReport())
                            .build());
            } else {
                FileDTO etsFileDTO = ftpFileDTOResult.getFileDTO();
                
                // 3. Validate file
                try {
                    validateFileDTO(etsFileDTO);
                } catch(Exception e) {
                    results.add(FileAttachmentMigrationError.builder()
                            .fileAttachmentUuid(attachment.getUuid())
                            .fileName(attachment.getFileName())
                            .fileContent(attachment.getFileContent())
                            .fileDTO(ftpFileDTOResult.getFileDTO())
                            .errorReport(e.getMessage())
                            .build());
                    continue;
                }
                
                // 4. update file attachment with actual values fetched from ETS FTP server
                attachment.setFileContent(etsFileDTO.getFileContent());
                attachment.setFileSize(etsFileDTO.getFileSize());
                attachment.setFileType(etsFileDTO.getFileType());
                attachment.setStatus(FileStatus.SUBMITTED);
                fileAttachmentRepository.save(attachment);
            }
        }
        
        return results;
    }
    
    private void validateFileDTO(FileDTO etsFileDTO) throws Exception {
        final long fileSize = etsFileDTO.getFileSize();
        if (fileSize <= FileConstants.MIN_FILE_SIZE) {
            throw new Exception(ErrorCode.MIN_FILE_SIZE_ERROR.getMessage());
        }
        if (fileSize >= FileConstants.MAX_FILE_SIZE) {
            throw new Exception(ErrorCode.MAX_FILE_SIZE_ERROR.getMessage());
        }
        
        try {
            fileScanService.scan(new ByteArrayInputStream(etsFileDTO.getFileContent()));
        }catch (Exception e) {
            throw new Exception(ErrorCode.INFECTED_STREAM.getMessage());
        }
    }
}
