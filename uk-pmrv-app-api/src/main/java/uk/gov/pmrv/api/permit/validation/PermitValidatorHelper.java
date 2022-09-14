package uk.gov.pmrv.api.permit.validation;

import java.util.List;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;

@UtilityClass
public class PermitValidatorHelper {

    public Object[] extractPermitViolations(List<PermitValidationResult> permitContextValidatorResults) {
        return permitContextValidatorResults.stream()
            .filter(permitValidationResult -> !permitValidationResult.isValid())
            .flatMap(permitValidationResult -> permitValidationResult.getPermitViolations().stream())
            .toArray();
    }
}
