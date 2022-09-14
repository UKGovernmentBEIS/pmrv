package uk.gov.pmrv.api.workflow.request.flow.aer.service.init;

import java.util.stream.Collectors;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivities;
import uk.gov.pmrv.api.workflow.request.flow.aer.mapper.AerMapper;

@Service
public class AerRegulatedActivitiesInitializationService implements AerSectionInitializationService {

    private static final AerMapper AER_MAPPER = Mappers.getMapper(AerMapper.class);

    @Override
    public void initialize(Aer aer, Permit permit) {

        aer.setRegulatedActivities(AerRegulatedActivities.builder()
            .regulatedActivities(
                permit.getRegulatedActivities().getRegulatedActivities().stream()
                    .map(AER_MAPPER::toAerRegulatedActivity)
                    .collect(Collectors.toList())
            ).build());
    }
}
