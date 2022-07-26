package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestPayloadType {

    INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD,
    PERMIT_ISSUANCE_REQUEST_PAYLOAD,
    PERMIT_SURRENDER_REQUEST_PAYLOAD,
    PERMIT_NOTIFICATION_REQUEST_PAYLOAD,
    PERMIT_REVOCATION_REQUEST_PAYLOAD,
    PERMIT_VARIATION_REQUEST_PAYLOAD,
    AER_REQUEST_PAYLOAD,

    SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD;
}
