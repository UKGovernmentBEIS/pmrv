package uk.gov.pmrv.api.migration.permit.envmanagementsystem;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationEnvironmentalManagementSystemMapper {

    EnvironmentalManagementSystem toEnvironmentalManagementSystem(EtsEnvironmentalManagementSystem etsEnvironmentalManagementSystem);
}
