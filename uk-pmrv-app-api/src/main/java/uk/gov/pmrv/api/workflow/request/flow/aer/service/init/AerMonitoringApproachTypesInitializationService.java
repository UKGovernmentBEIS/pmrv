package uk.gov.pmrv.api.workflow.request.flow.aer.service.init;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.reporting.domain.Aer;

@Service
public class AerMonitoringApproachTypesInitializationService implements AerSectionInitializationService {

    @Override
    public void initialize(Aer aer, Permit permit) {
        aer.setMonitoringApproachTypes(permit.getMonitoringApproaches().getMonitoringApproaches().keySet());
    }
}
