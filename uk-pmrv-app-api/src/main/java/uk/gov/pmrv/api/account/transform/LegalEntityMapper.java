package uk.gov.pmrv.api.account.transform;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

/**
 * The Legal Entity Mapper.
 */
@Mapper(componentModel = "spring", uses = LocationMapper.class, config = MapperConfig.class)
public interface LegalEntityMapper {

    @Mapping(target = "location.address", source = "address")
    LegalEntity toLegalEntity(LegalEntityDTO legalEntityDTO);
    
    @Mapping(target = "address", source = "location.address")
    LegalEntityDTO toLegalEntityDTO(LegalEntity legalEntity);

    LegalEntityInfoDTO toLegalEntityInfoDTO(LegalEntity legalEntity);
    
    List<LegalEntityInfoDTO> toLegalEntityInfoDTOs(List<LegalEntity> legalEntities);

}
