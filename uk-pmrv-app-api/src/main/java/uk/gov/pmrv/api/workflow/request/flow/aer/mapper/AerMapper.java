package uk.gov.pmrv.api.workflow.request.flow.aer.mapper;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivity;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AerMapper {

    AerRegulatedActivity toAerRegulatedActivity(RegulatedActivity regulatedActivity);
}
