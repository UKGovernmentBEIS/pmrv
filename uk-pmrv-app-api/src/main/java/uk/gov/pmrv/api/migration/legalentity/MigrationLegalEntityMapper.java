package uk.gov.pmrv.api.migration.legalentity;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationLegalEntityMapper {

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "address.line1", source = "line1")
    @Mapping(target = "address.line2", source = "line2")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.postcode", source = "postcode")
    @Mapping(target = "address.country", source = "country")
    LegalEntityDTO toLegalEntityDTO(LegalEntityVO legalEntityVO);

    @AfterMapping
    default void setType(@MappingTarget LegalEntityDTO legalEntityDTO, LegalEntityVO legalEntityVO) {
        switch (legalEntityVO.getType()){
            case "Company / Corporate Body":
                legalEntityDTO.setType(LegalEntityType.LIMITED_COMPANY);
                break;
            case "Partnership":
                legalEntityDTO.setType(LegalEntityType.PARTNERSHIP);
                break;
        }
    }
    
    @AfterMapping
    default void setNoReferenceNumberReason(@MappingTarget LegalEntityDTO legalEntityDTO, LegalEntityVO legalEntityVO) {
        if(ObjectUtils.isEmpty(legalEntityVO.getReferenceNumber())) {
            legalEntityDTO.setNoReferenceNumberReason("N/A");
        }
    }
}
