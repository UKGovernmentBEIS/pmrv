package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationOperatorSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.mapper.PermitVariationMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationOperatorSubmitService {

	private final RequestService requestService;
	private final RequestTaskService requestTaskService;
	private final PermitValidatorService permitValidatorService;
    private final PermitVariationDetailsValidator permitVariationDetailsValidator;
	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	private final PermitVariationMapper permitVariationMapper = Mappers.getMapper(PermitVariationMapper.class);

	@Transactional
	public void savePermitVariation(
			PermitVariationOperatorSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
		PermitVariationApplicationSubmitRequestTaskPayload taskPayload = (PermitVariationApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		taskPayload.setPermitVariationDetails(taskActionPayload.getPermitVariationDetails());
		taskPayload.setPermitVariationDetailsCompleted(taskActionPayload.getPermitVariationDetailsCompleted());
		taskPayload.setPermit(taskActionPayload.getPermit());
		taskPayload.setPermitSectionsCompleted(taskActionPayload.getPermitSectionsCompleted());
		taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());

		requestTaskService.saveRequestTask(requestTask);
	}
	
	@Transactional
	public void submitPermitVariation(RequestTask requestTask, PmrvUser authUser) {
		PermitVariationApplicationSubmitRequestTaskPayload taskPayload = (PermitVariationApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		final Request request = requestTask.getRequest();
		final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
				.getInstallationOperatorDetails(request.getAccountId());
		
		final PermitContainer permitContainer = permitVariationMapper.toPermitContainer(taskPayload, installationOperatorDetails);
		
		//validate permit
		permitValidatorService.validatePermit(permitContainer);
		permitVariationDetailsValidator.validate(taskPayload.getPermitVariationDetails());
		
		//save permit to request payload
		PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
		requestPayload.setPermitVariationDetails(taskPayload.getPermitVariationDetails());
		requestPayload.setPermitVariationDetailsCompleted(taskPayload.getPermitVariationDetailsCompleted());
        requestPayload.setPermitType(taskPayload.getPermitType());
		requestPayload.setPermit(taskPayload.getPermit());
		requestPayload.setPermitSectionsCompleted(taskPayload.getPermitSectionsCompleted());
		requestPayload.setPermitAttachments(taskPayload.getPermitAttachments());
		requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
		requestService.saveRequest(request);
		
		//add request action
		PermitVariationApplicationSubmittedRequestActionPayload actionPayload = permitVariationMapper
				.toPermitVariationApplicationSubmittedRequestActionPayload(taskPayload, installationOperatorDetails);
        requestService.addActionToRequest(request, actionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_SUBMITTED, authUser.getUserId());
	}
}
