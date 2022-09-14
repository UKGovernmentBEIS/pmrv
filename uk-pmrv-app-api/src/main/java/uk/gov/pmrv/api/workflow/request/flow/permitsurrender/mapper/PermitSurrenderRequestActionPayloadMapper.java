package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitSurrenderRequestActionPayloadMapper {

    @Mapping(target = "reviewDetermination.reason", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitSurrenderApplicationGrantedRequestActionPayload cloneGrantedPayloadIgnoreReason(
            PermitSurrenderApplicationGrantedRequestActionPayload requestActionPayload);
    
    @Mapping(target = "reviewDetermination.reason", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitSurrenderApplicationRejectedRequestActionPayload cloneRejectedPayloadIgnoreReason(
            PermitSurrenderApplicationRejectedRequestActionPayload requestActionPayload);
    
    @Mapping(target = "reviewDetermination.reason", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReason(
            PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload requestActionPayload);

    @Mapping(target = "cessation.notes", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitCessationCompletedRequestActionPayload cloneCessationCompletedPayloadIgnoreNotes(
        PermitCessationCompletedRequestActionPayload requestActionPayload);
}
