package uk.gov.pmrv.api.migration.permit.abbreviations;

import java.util.List;
import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.abbreviations.AbbreviationDefinition;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationAbbreviationsMapper {

    AbbreviationDefinition toAbbreviationDefinition(EtsAbbreviation etsAbbreviation);

    List<AbbreviationDefinition> toAbbreviationDefinitions(List<EtsAbbreviation> etsAbbreviations);
}
