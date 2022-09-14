package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningAmendApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitSaveCessationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentCancelRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMarkAsReceivedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpExtendDateRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpSaveResponseRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationOperatorSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponseSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmitRequestTaskActionPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningAmendApplicationRequestTaskActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitIssuanceSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveApplicationReviewRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD"),

    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSaveCessationRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_CESSATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpExtendDateRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSaveResponseRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationWithdrawRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_WITHDRAW_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSaveCessationRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_SAVE_CESSATION_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitVariationOperatorSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveApplicationReviewRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveReviewDeterminationRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD"),

    @JsonSubTypes.Type(value = AerSaveApplicationRequestTaskActionPayload.class, name = "AER_SAVE_APPLICATION_PAYLOAD"),

    @JsonSubTypes.Type(value = RfiSubmitRequestTaskActionPayload.class, name = "RFI_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = RfiResponseSubmitRequestTaskActionPayload.class, name = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = RdeSubmitRequestTaskActionPayload.class, name = "RDE_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeForceDecisionRequestTaskActionPayload.class, name = "RDE_FORCE_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeResponseSubmitRequestTaskActionPayload.class, name = "RDE_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PaymentMarkAsReceivedRequestTaskActionPayload.class, name = "PAYMENT_MARK_AS_RECEIVED_PAYLOAD"),
    @JsonSubTypes.Type(value = PaymentCancelRequestTaskActionPayload.class, name = "PAYMENT_CANCEL_PAYLOAD"),

    @JsonSubTypes.Type(value = RequestTaskActionEmptyPayload.class, name = "EMPTY_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestTaskActionPayload {

    private RequestTaskActionPayloadType payloadType;
}
