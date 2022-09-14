package uk.gov.pmrv.api.workflow.request.flow.aer.service.init;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.reporting.domain.Aer;

public interface AerSectionInitializationService {

    void initialize(Aer aer, Permit permit);
}
