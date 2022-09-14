package uk.gov.pmrv.api.verificationbody.domain.dto.validation;

import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StatusPendingValidator implements ConstraintValidator<StatusPending, VerificationBodyStatus> {

    @Override
    public boolean isValid(VerificationBodyStatus status, ConstraintValidatorContext context) {
        return !VerificationBodyStatus.PENDING.equals(status);
    }
}
