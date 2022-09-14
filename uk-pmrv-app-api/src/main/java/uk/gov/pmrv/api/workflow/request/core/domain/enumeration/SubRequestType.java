package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubRequestType {

    APPLICATION_REVIEW("applicationReview"),
    RFI("rfi"),
    RDE("rde"),
    FOLLOW_UP_RESPONSE("followUpResponse"),
    PAYMENT("payment"),
    AER("aer"),
    ;
    
    private final String code;
}
