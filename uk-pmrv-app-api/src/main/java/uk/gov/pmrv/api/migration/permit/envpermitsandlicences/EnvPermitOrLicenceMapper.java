package uk.gov.pmrv.api.migration.permit.envpermitsandlicences;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EnvPermitOrLicenceMapper {

    List<uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvPermitOrLicence> toPermitEnvPermitOrLicences(List<EnvPermitOrLicence> etsEnvPermitOrLicence);
    
    @Mapping(target = "num", source = "permitNumber")
    @Mapping(target = "type", source = "permitType")
    @Mapping(target = "permitHolder", source = "permitHolder")
    @Mapping(target = "issuingAuthority", source = "issuingAuthority")
    uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvPermitOrLicence toPermitEnvPermitOrLicence(EnvPermitOrLicence etsEnvPermitOrLicence);
}
