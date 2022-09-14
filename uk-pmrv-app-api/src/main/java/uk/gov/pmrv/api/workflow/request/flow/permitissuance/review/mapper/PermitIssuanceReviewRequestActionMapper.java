package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationRejectedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitIssuanceReviewRequestActionMapper {

    @Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "reviewGroupDecisions", ignore = true)
    PermitIssuanceApplicationGrantedRequestActionPayload cloneGrantedPayloadIgnoreReasonAndDecisions(
        PermitIssuanceApplicationGrantedRequestActionPayload payload);
    
    @Mapping(target = "determination.reason", ignore = true)
    PermitIssuanceApplicationRejectedRequestActionPayload cloneRejectedPayloadIgnoreReason(
        PermitIssuanceApplicationRejectedRequestActionPayload payload);
    
    @Mapping(target = "determination.reason", ignore = true)
    PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReason(
        PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload payload);
}
