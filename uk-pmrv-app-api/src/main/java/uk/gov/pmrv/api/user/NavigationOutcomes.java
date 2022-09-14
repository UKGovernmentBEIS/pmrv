package uk.gov.pmrv.api.user;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NavigationOutcomes {
	public final String REGISTRATION_EMAIL_VERIFY_CONFIRMATION_URL = "registration/email-confirmed";
	public final String OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL = "registration/invitation";
	public final String REGULATOR_REGISTRATION_INVITATION_ACCEPTED_URL = "invitation/regulator";
	public final String VERIFIER_REGISTRATION_INVITATION_ACCEPTED_URL = "invitation/verifier";
	public final String CHANGE_2FA_URL = "2fa/request-change";
}
