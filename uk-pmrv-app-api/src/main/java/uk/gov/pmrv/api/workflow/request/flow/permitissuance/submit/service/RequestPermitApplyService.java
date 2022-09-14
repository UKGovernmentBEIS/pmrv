package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.mapper.PermitSubmitMapper;

@Service
@RequiredArgsConstructor
public class RequestPermitApplyService {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final PermitValidatorService permitValidatorService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final PermitSubmitMapper permitSubmitMapper = Mappers.getMapper(PermitSubmitMapper.class);

    @Transactional
    public void applySaveAction(
        PermitIssuanceSaveApplicationRequestTaskActionPayload permitIssuanceSaveApplicationRequestTaskActionPayload, RequestTask requestTask) {
        PermitIssuanceApplicationSubmitRequestTaskPayload
            permitIssuanceApplicationSubmitRequestTaskPayload = (PermitIssuanceApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        permitIssuanceApplicationSubmitRequestTaskPayload.setPermitType(permitIssuanceSaveApplicationRequestTaskActionPayload.getPermitType());
        permitIssuanceApplicationSubmitRequestTaskPayload.setPermit(permitIssuanceSaveApplicationRequestTaskActionPayload.getPermit());
        permitIssuanceApplicationSubmitRequestTaskPayload.setPermitSectionsCompleted(permitIssuanceSaveApplicationRequestTaskActionPayload.getPermitSectionsCompleted());

        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void applySubmitAction(RequestTask requestTask, PmrvUser authUser) {
        Request request = requestTask.getRequest();
        PermitIssuanceApplicationSubmitRequestTaskPayload
            permitIssuanceApplicationSubmitRequestTaskPayload = (PermitIssuanceApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        PermitContainer permitContainer = permitSubmitMapper.toPermitContainer(
            permitIssuanceApplicationSubmitRequestTaskPayload);
        permitValidatorService.validatePermit(permitContainer);

        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(
            request.getAccountId());
        PermitIssuanceRequestPayload permitIssuanceRequestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        permitIssuanceRequestPayload.setPermitType(permitIssuanceApplicationSubmitRequestTaskPayload.getPermitType());
        permitIssuanceRequestPayload.setPermit(permitIssuanceApplicationSubmitRequestTaskPayload.getPermit());
        permitIssuanceRequestPayload.setPermitAttachments(permitIssuanceApplicationSubmitRequestTaskPayload.getPermitAttachments());
        permitIssuanceRequestPayload.setPermitSectionsCompleted(permitIssuanceApplicationSubmitRequestTaskPayload.getPermitSectionsCompleted());
        requestService.saveRequest(request);

        PermitIssuanceApplicationSubmittedRequestActionPayload permitApplySubmittedPayload = permitSubmitMapper.toPermitIssuanceApplicationSubmittedRequestActionPayload(
            permitIssuanceApplicationSubmitRequestTaskPayload);
        permitApplySubmittedPayload.setPermitAttachments(permitIssuanceRequestPayload.getPermitAttachments());
        permitApplySubmittedPayload.setInstallationOperatorDetails(installationOperatorDetails);

        requestService.addActionToRequest(request, permitApplySubmittedPayload, RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED, authUser.getUserId());
    }
}
