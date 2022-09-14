package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestCreateActionType {

    INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION(RequestType.INSTALLATION_ACCOUNT_OPENING),
    PERMIT_SURRENDER(RequestType.PERMIT_SURRENDER),
    PERMIT_REVOCATION(RequestType.PERMIT_REVOCATION),
    PERMIT_VARIATION(RequestType.PERMIT_VARIATION),
    PERMIT_TRANSFER(RequestType.PERMIT_TRANSFER),
    PERMIT_NOTIFICATION(RequestType.PERMIT_NOTIFICATION),
    AER(RequestType.AER);

    private final RequestType type;
}
