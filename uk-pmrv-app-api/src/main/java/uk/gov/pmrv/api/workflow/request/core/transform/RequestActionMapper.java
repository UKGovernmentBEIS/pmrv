package uk.gov.pmrv.api.workflow.request.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;

/**
 * The Request Mapper.
 * Note: the dtos returned are not deep copies. Thus, modifications inside the properties of the dto will also 
 * modify the entity.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RequestActionMapper {
    
    @Mapping(target = "id", source = "requestAction.id")
    RequestActionInfoDTO toRequestActionInfoDTO(RequestAction requestAction);

    @Mapping(target = "requestAccountId", source = "request.accountId")
    RequestActionDTO toRequestActionDTO(RequestAction requestAction);

    @Mapping(target = "requestAccountId", source = "request.accountId")
    @Mapping(target = "payload", ignore = true)
    RequestActionDTO toRequestActionDTOIgnorePayload(RequestAction requestAction);
}
