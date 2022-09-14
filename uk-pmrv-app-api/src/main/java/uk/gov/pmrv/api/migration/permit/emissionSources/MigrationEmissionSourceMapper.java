package uk.gov.pmrv.api.migration.permit.emissionSources;

import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {UUID.class})
public interface MigrationEmissionSourceMapper {

    List<EmissionSource> toEmissionSources(List<EtsEmissionSource> etsEmissionSources);

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    EmissionSource toEmissionSource(EtsEmissionSource etsEmissionSource);
}
