package uk.gov.pmrv.api.web.util;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;

import java.io.IOException;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FileDtoMapper {

    @Mapping(target = "fileName", source = "originalFilename")
    @Mapping(target = "fileSize", source = "size")
    @Mapping(target = "fileContent", source = "bytes")
    FileDTO toFileDTO(MultipartFile file) throws IOException;

    @AfterMapping
    default void setFileType(@MappingTarget FileDTO fileDTO, MultipartFile file) {
        ContentInfo match = new ContentInfoUtil().findMatch(fileDTO.getFileContent());
        String fileType;
        if (match != null) {
            fileType = match.getContentType().getMimeType();
        } else {
            fileType = file.getContentType();
        }
        fileDTO.setFileType(fileType);
    }
}
