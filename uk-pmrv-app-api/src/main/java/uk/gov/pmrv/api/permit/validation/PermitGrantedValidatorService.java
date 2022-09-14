package uk.gov.pmrv.api.permit.validation;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;

@Service
@Validated
@RequiredArgsConstructor
public class PermitGrantedValidatorService {

    private final List<PermitGrantedContextValidator> permitGrantedContextValidators;

    public void validatePermit(@Valid PermitContainer permitContainer) {
        List<PermitValidationResult> validatorResults = new ArrayList<>();
        permitGrantedContextValidators.forEach(v -> validatorResults.add(v.validate(permitContainer)));

        boolean isValid = validatorResults.stream().allMatch(PermitValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT, PermitValidatorHelper.extractPermitViolations(validatorResults));
        }
    }
}
