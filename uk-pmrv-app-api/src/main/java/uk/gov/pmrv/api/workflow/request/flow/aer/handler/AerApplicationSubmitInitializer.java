package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.init.AerSectionInitializationService;

@Service
@AllArgsConstructor
public class AerApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final PermitQueryService permitQueryService;
    private final List<AerSectionInitializationService> aerSectionInitializationServices;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        
        final Long accountId = request.getAccountId();
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(accountId);
        final Permit permit = permitQueryService.getPermitContainerByAccountId(accountId).getPermit();

        return AerApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .aer(initializeAer(permit))
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AER_APPLICATION_SUBMIT);
    }

    private Aer initializeAer(Permit permit) {
        Aer aer = new Aer();
        aerSectionInitializationServices.forEach(sectionInitializer -> sectionInitializer.initialize(aer, permit));
        return aer;
    }
}
