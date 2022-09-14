package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.GrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitReviewMapper {

	PermitContainer toPermitContainer(PermitIssuanceApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload);

    @AfterMapping
    default void setActivationDate(@MappingTarget PermitContainer permitContainer, PermitIssuanceApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload) {
    	if(applicationReviewRequestTaskPayload.getDetermination() != null &&
    			DeterminationType.GRANTED == applicationReviewRequestTaskPayload.getDetermination().getType()) {
            permitContainer.setActivationDate(((GrantDetermination) applicationReviewRequestTaskPayload.getDetermination()).getActivationDate());
        }
    }

    @AfterMapping
    default void setAnnualEmissionsTargets(@MappingTarget PermitContainer permitContainer, PermitIssuanceApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload) {
    	if(applicationReviewRequestTaskPayload.getDetermination() != null &&
    			DeterminationType.GRANTED == applicationReviewRequestTaskPayload.getDetermination().getType()) {
            permitContainer.setAnnualEmissionsTargets(((GrantDetermination) applicationReviewRequestTaskPayload.getDetermination()).getAnnualEmissionsTargets());
        }
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    PermitIssuanceApplicationReviewRequestTaskPayload toPermitIssuanceApplicationReviewRequestTaskPayload(
        PermitIssuanceRequestPayload permitIssuanceRequestPayload, RequestTaskPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "reviewAttachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    PermitIssuanceApplicationAmendsSubmitRequestTaskPayload toPermitIssuanceApplicationAmendsSubmitRequestTaskPayload(
            PermitIssuanceRequestPayload permitIssuanceRequestPayload);

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget PermitIssuanceApplicationAmendsSubmitRequestTaskPayload requestTaskPayload,
                                           PermitIssuanceRequestPayload payload) {
        Set<UUID> amendFiles = requestTaskPayload.getReviewGroupDecisions().values().stream()
            .map(permitIssuanceReviewDecision -> permitIssuanceReviewDecision.getRequiredChange().getFiles())
            .flatMap(Collection::stream).collect(Collectors.toSet());

        Map<UUID, String> reviewFiles = payload.getReviewAttachments().entrySet().stream()
                .filter(entry -> amendFiles.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        requestTaskPayload.setReviewAttachments(reviewFiles);
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_GRANTED_PAYLOAD)")
    PermitIssuanceApplicationGrantedRequestActionPayload toPermitIssuanceApplicationGrantedRequestActionPayload(
        PermitIssuanceRequestPayload requestPayload);

    @AfterMapping
    default void setGrantDetermination(@MappingTarget PermitIssuanceApplicationGrantedRequestActionPayload grantDeterminationRequestActionPayload, PermitIssuanceRequestPayload requestPayload) {
        if(DeterminationType.GRANTED.equals(requestPayload.getDetermination().getType())){
        	grantDeterminationRequestActionPayload.setDetermination((PermitIssuanceGrantDetermination) requestPayload.getDetermination());
        }
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_REJECTED_PAYLOAD)")
    PermitIssuanceApplicationRejectedRequestActionPayload toPermitIssuanceApplicationRejectedRequestActionPayload(
        PermitIssuanceRequestPayload requestPayload);

    @AfterMapping
    default void setRejectedDetermination(@MappingTarget PermitIssuanceApplicationRejectedRequestActionPayload rejectDeterminationRequestActionPayload, PermitIssuanceRequestPayload requestPayload) {
        if(DeterminationType.REJECTED == requestPayload.getDetermination().getType()){
        	rejectDeterminationRequestActionPayload.setDetermination((PermitIssuanceRejectDetermination) requestPayload.getDetermination());
        }
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)")
    PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload toPermitIssuanceApplicationDeemedWithdrawnRequestActionPayload(
        PermitIssuanceRequestPayload requestPayload);

    @AfterMapping
    default void setDeemedWithdrawnDetermination(@MappingTarget PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload deemedWithdrawnDeterminationRequestActionPayload, PermitIssuanceRequestPayload requestPayload) {
        if(DeterminationType.DEEMED_WITHDRAWN == requestPayload.getDetermination().getType()){
        	deemedWithdrawnDeterminationRequestActionPayload.setDetermination((PermitIssuanceDeemedWithdrawnDetermination) requestPayload.getDetermination());
        }
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    PermitIssuanceApplicationReturnedForAmendsRequestActionPayload toPermitIssuanceApplicationReturnedForAmendsRequestActionPayload(
            PermitIssuanceApplicationReviewRequestTaskPayload payload);

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget PermitIssuanceApplicationReturnedForAmendsRequestActionPayload actionPayload,
                         PermitIssuanceApplicationReviewRequestTaskPayload payload) {
        Set<UUID> amendFiles = actionPayload.getReviewGroupDecisions().values().stream()
            .map(permitIssuanceReviewDecision -> permitIssuanceReviewDecision.getRequiredChange().getFiles())
            .flatMap(Collection::stream).collect(Collectors.toSet());

        Map<UUID, String> reviewFiles = payload.getReviewAttachments().entrySet().stream()
                .filter(entry -> amendFiles.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        actionPayload.setReviewAttachments(reviewFiles);
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<PermitReviewGroup, PermitIssuanceReviewDecision> setReviewGroupDecisionsForOperatorAmend(Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecision) {
        return reviewGroupDecision.entrySet().stream()
                .filter(entry -> entry.getValue().getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)).map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(),
                        PermitIssuanceReviewDecision.builder()
                            .type(entry.getValue().getType())
                            .requiredChange(entry.getValue().getRequiredChange())
                            .build())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
