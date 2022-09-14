package uk.gov.pmrv.api.files.common.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.FileEntity;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FileMapper {

    FileDTO toFileDTO(FileEntity fileEntity);
}
