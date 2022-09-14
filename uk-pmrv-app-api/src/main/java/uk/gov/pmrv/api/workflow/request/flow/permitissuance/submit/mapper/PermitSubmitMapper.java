package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitSubmitMapper {
    
    PermitContainer toPermitContainer(
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssuanceApplicationSubmitRequestTaskPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "permitAttachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    PermitIssuanceApplicationSubmittedRequestActionPayload toPermitIssuanceApplicationSubmittedRequestActionPayload(
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssuanceApplicationSubmitRequestTaskPayload);
}
