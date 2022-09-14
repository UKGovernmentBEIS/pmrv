package uk.gov.pmrv.api.authorization;

import lombok.experimental.UtilityClass;

/**
 * Encapsulates constants related to Authorities
 */
@UtilityClass
public class AuthorityConstants {

    public static final String REGULATOR_ADMIN_TEAM_ROLE_CODE = "regulator_admin_team";
    public static final String REGULATOR_TECHNICAL_OFFICER_ROLE_CODE = "regulator_technical_officer";
    public static final String CA_SUPER_USER_ROLE_CODE = "ca_super_user";
    public static final String PMRV_SUPER_USER_ROLE_CODE = "pmrv_super_user";
    public static final String OPERATOR_ADMIN_ROLE_CODE = "operator_admin";
    public static final String OPERATOR_ROLE_CODE = "operator";
    public static final String VERIFIER_ADMIN_ROLE_CODE = "verifier_admin";
    public static final String CONSULTANT_AGENT = "consultant_agent";
    public static final String EMITTER_CONTACT = "emitter_contact";
}
