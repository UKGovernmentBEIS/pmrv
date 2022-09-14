package uk.gov.pmrv.api.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtTokenActionEnum {

    USER_REGISTRATION("user_registration", "user_email"),
    OPERATOR_INVITATION("operator_invitation", "authority_uuid"),
    REGULATOR_INVITATION("regulator_invitation", "authority_uuid"),
    VERIFIER_INVITATION("verifier_invitation", "authority_uuid"),
    CHANGE_2FA("change_2fa", "user_email"),
    GET_FILE("get_file", "get_file_uuid")
    ;
    
    private final String subject;
    private final String claimName;
    
}
