package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitSurrenderMapper {

    PermitSurrenderContainer toPermitSurrenderContainer(
            PermitSurrenderApplicationSubmitRequestTaskPayload permitSurrenderApplicationSubmitRequestTaskPayload);
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "permitSurrenderAttachments", ignore = true)
    PermitSurrenderApplicationSubmittedRequestActionPayload toApplicationSubmittedRequestActionPayload(
            PermitSurrenderApplicationSubmitRequestTaskPayload permitSurrenderApplicationSubmitRequestTaskPayload);
    
    @Mapping(target = "payloadType", source = "payloadType")
    PermitSurrenderApplicationReviewRequestTaskPayload toApplicationReviewRequestTaskPayload(
        PermitSurrenderRequestPayload requestPayload, RequestTaskPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD)")
    PermitSurrenderApplicationGrantedRequestActionPayload toPermitSurrenderApplicationGrantedRequestActionPayload(
        PermitSurrenderRequestPayload requestPayload);
    
    @AfterMapping
    default void setGrantDeterminationDetails(@MappingTarget PermitSurrenderReviewDeterminationGrant grantDeterminationTarget, PermitSurrenderReviewDetermination determination) {
        if(determination instanceof PermitSurrenderReviewDeterminationGrant){
            PermitSurrenderReviewDeterminationGrant determinationSource = (PermitSurrenderReviewDeterminationGrant)determination;
            grantDeterminationTarget.setStopDate(determinationSource.getStopDate());
            grantDeterminationTarget.setNoticeDate(determinationSource.getNoticeDate());
            grantDeterminationTarget.setReportRequired(determinationSource.getReportRequired());
            grantDeterminationTarget.setReportDate(determinationSource.getReportDate());
            grantDeterminationTarget.setAllowancesSurrenderRequired(determinationSource.getAllowancesSurrenderRequired());
            grantDeterminationTarget.setAllowancesSurrenderDate(determinationSource.getAllowancesSurrenderDate());
        }
    }
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD)")
    PermitSurrenderApplicationRejectedRequestActionPayload toPermitSurrenderApplicationRejectedRequestActionPayload(
        PermitSurrenderRequestPayload requestPayload);
    
    @AfterMapping
    default void setRejectDeterminationDetails(@MappingTarget PermitSurrenderReviewDeterminationReject rejectDeterminationTarget, PermitSurrenderReviewDetermination determination) {
        if(determination instanceof PermitSurrenderReviewDeterminationReject){
            PermitSurrenderReviewDeterminationReject determinationSource = (PermitSurrenderReviewDeterminationReject)determination;
            rejectDeterminationTarget.setOfficialRefusalLetter(determinationSource.getOfficialRefusalLetter());
            rejectDeterminationTarget.setShouldFeeBeRefundedToOperator(determinationSource.getShouldFeeBeRefundedToOperator());
        }
    }
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)")
    PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload toPermitSurrenderApplicationDeemedWithdrawnRequestActionPayload(
        PermitSurrenderRequestPayload requestPayload);

}
