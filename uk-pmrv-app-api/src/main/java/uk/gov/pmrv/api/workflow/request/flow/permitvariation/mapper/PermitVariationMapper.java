package uk.gov.pmrv.api.workflow.request.flow.permitvariation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitVariationMapper {

	@Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
	PermitContainer toPermitContainer(PermitVariationApplicationSubmitRequestTaskPayload payload, InstallationOperatorDetails installationOperatorDetails);
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
	@Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    PermitVariationApplicationSubmittedRequestActionPayload toPermitVariationApplicationSubmittedRequestActionPayload(
        PermitVariationApplicationSubmitRequestTaskPayload permitVariationApplicationSubmitRequestTaskPayload, InstallationOperatorDetails installationOperatorDetails);
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)")
    PermitVariationApplicationReviewRequestTaskPayload toPermitVariationApplicationReviewRequestTaskPayload(
        PermitVariationRequestPayload permitVariationRequestPayload);
	
}
