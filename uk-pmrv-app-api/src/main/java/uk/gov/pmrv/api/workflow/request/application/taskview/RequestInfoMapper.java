package uk.gov.pmrv.api.workflow.request.application.taskview;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RequestInfoMapper {

    @Mapping(target = "requestMetadata", source = "metadata")
    RequestInfoDTO toRequestInfoDTO(Request request);
}
