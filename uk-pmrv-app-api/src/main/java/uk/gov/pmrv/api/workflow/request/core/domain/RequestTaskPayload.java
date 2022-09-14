package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentConfirmRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentTrackRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationWaitForFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningApplicationRequestTaskPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD"),
    
    @JsonSubTypes.Type(value = PermitIssuanceApplicationSubmitRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_WAIT_FOR_AMENDS_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationAmendsSubmitRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitSurrenderApplicationReviewRequestTaskPayload.class, name = "PERMIT_SURRENDER_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderApplicationReviewRequestTaskPayload.class, name = "PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderApplicationReviewRequestTaskPayload.class, name = "PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderApplicationSubmitRequestTaskPayload.class, name = "PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitCessationSubmitRequestTaskPayload.class, name = "PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitNotificationApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationApplicationSubmitRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationWaitForFollowUpRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD"),
    
    @JsonSubTypes.Type(value = PermitCessationSubmitRequestTaskPayload.class, name = "PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationPeerReviewRequestTaskPayload.class, name = "PERMIT_REVOCATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationPeerReviewRequestTaskPayload.class, name = "PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationSubmitRequestTaskPayload.class, name = "PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationWaitForAppealRequestTaskPayload.class, name = "PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD"),
    
    @JsonSubTypes.Type(value = PermitVariationApplicationSubmitRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationReviewRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD"),

    @JsonSubTypes.Type(value = AerApplicationSubmitRequestTaskPayload.class, name = "AER_APPLICATION_SUBMIT_PAYLOAD"),
    
    @JsonSubTypes.Type(value = SystemMessageNotificationRequestTaskPayload.class, name = "SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD"),
    
    @JsonSubTypes.Type(value = RfiResponseSubmitRequestTaskPayload.class, name = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = RdeForceDecisionRequestTaskPayload.class, name = "RDE_WAIT_FOR_RESPONSE_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeResponseRequestTaskPayload.class, name = "RDE_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PaymentMakeRequestTaskPayload.class, name = "PAYMENT_MAKE_PAYLOAD"),
    @JsonSubTypes.Type(value = PaymentTrackRequestTaskPayload.class, name = "PAYMENT_TRACK_PAYLOAD"),
    @JsonSubTypes.Type(value = PaymentConfirmRequestTaskPayload.class, name = "PAYMENT_CONFIRM_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestTaskPayload {

    private RequestTaskPayloadType payloadType;

    @JsonIgnore
    public Map<UUID, String> getAttachments() {
        return Collections.emptyMap();
    }

    @JsonIgnore
    public Set<UUID> getReferencedAttachmentIds() {
        return Collections.emptySet();
    }

    @JsonIgnore
    public void removeAttachments(final Collection<UUID> uuids) {

        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        this.getAttachments().keySet().removeIf(uuids::contains);
    }
}
