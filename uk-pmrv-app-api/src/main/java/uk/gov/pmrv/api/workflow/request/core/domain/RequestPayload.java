package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningRequestPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceRequestPayload.class, name = "PERMIT_ISSUANCE_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderRequestPayload.class, name = "PERMIT_SURRENDER_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationRequestPayload.class, name = "PERMIT_NOTIFICATION_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationRequestPayload.class, name = "PERMIT_REVOCATION_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationRequestPayload.class, name = "PERMIT_VARIATION_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AerRequestPayload.class, name = "AER_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = SystemMessageNotificationRequestPayload.class, name = "SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestPayload implements Payload {

    private RequestPayloadType payloadType;

    private String operatorAssignee;

    private String regulatorAssignee;
    
    private String verifierAssignee;

    private String regulatorReviewer;

    private String regulatorPeerReviewer;

    private BigDecimal paymentAmount;
}
