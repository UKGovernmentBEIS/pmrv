package uk.gov.pmrv.api.migration.files;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EtsFileAttachmentMapper {

    @Mapping(target = "uuid", expression = "java(etsFileAttachment.getUuid().toString())")
    @Mapping(target = "fileName", source = "uploadedFileName")
    // temporarily save with bytes of the file name
    @Mapping(target = "fileContent", expression = "java(etsFileAttachment.getStoredFileName().getBytes())")
    // temporarily save the file name length
    @Mapping(target = "fileSize", expression = "java(etsFileAttachment.getStoredFileName().length())")
    // temporarily save the type of the file name
    @Mapping(target = "fileType", expression = "java(org.springframework.util.StringUtils.getFilenameExtension(etsFileAttachment.getUploadedFileName()))")
    @Mapping(target = "status", expression = "java(uk.gov.pmrv.api.files.common.domain.FileStatus.PENDING_MIGRATION)")
    @Mapping(target = "createdBy", expression = "java(uk.gov.pmrv.api.migration.MigrationConstants.MIGRATION_PROCESS_USER)")
    FileAttachment toFileAttachment(EtsFileAttachment etsFileAttachment);

    List<FileAttachment> toFileAttachments(List<EtsFileAttachment> etsFileAttachments);
}
