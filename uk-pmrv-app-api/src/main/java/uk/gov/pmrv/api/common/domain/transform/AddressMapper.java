package uk.gov.pmrv.api.common.domain.transform;

import org.mapstruct.Mapper;

import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AddressMapper {

    Address toAddress(AddressDTO addressDto);
}
