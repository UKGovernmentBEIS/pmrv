package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionForcedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InstallationAccountOpeningApplicationSubmittedRequestActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = InstallationAccountOpeningApprovedRequestActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = InstallationAccountOpeningDecisionRequestActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD"),
    
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_ISSUANCE_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationGrantedRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationRejectedRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationReturnedForAmendsRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationSubmittedRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD"),
    
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_SURRENDER_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationGrantedRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationRejectedRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationSubmittedRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitCessationCompletedRequestActionPayload.class, name = "PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD"),
    
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_NOTIFICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationApplicationSubmittedRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationFollowUpResponseSubmittedRequestActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD"),

        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_REVOCATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitCessationCompletedRequestActionPayload.class, name = "PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitRevocationApplicationSubmittedRequestActionPayload.class, name = "PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitRevocationApplicationWithdrawnRequestActionPayload.class, name = "PERMIT_REVOCATION_APPLICATION_WITHDRAWN_PAYLOAD"),
        
        @JsonSubTypes.Type(value = PermitVariationApplicationSubmittedRequestActionPayload.class, name = "PERMIT_VARIATION_APPLICATION_SUBMITTED_PAYLOAD"),
    
        @JsonSubTypes.Type(value = RfiResponseSubmittedRequestActionPayload.class, name = "RFI_RESPONSE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = RfiSubmittedRequestActionPayload.class, name = "RFI_SUBMITTED_PAYLOAD"),
    
        @JsonSubTypes.Type(value = RdeDecisionForcedRequestActionPayload.class, name = "RDE_DECISION_FORCED_PAYLOAD"),
        @JsonSubTypes.Type(value = RdeRejectedRequestActionPayload.class, name = "RDE_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = RdeSubmittedRequestActionPayload.class, name = "RDE_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_MARKED_AS_PAID_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_MARKED_AS_RECEIVED_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentCancelledRequestActionPayload.class, name = "PAYMENT_CANCELLED_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestActionPayload {

    private RequestActionPayloadType payloadType;

    @JsonIgnore
    public Map<UUID, String> getAttachments() {
        return Collections.emptyMap();
    }
    
    @JsonIgnore
    public Map<UUID, String> getFileDocuments() {
        return Collections.emptyMap();
    }

}
