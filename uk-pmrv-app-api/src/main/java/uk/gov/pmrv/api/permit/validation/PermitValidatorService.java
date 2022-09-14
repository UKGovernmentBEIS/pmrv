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
public class PermitValidatorService {

    private final List<PermitContextValidator> permitContextValidators;
    
    public void validatePermit(@Valid PermitContainer permitContainer) {
        List<PermitValidationResult> permitContextValidatorResults = new ArrayList<>();
        permitContextValidators.forEach(v -> permitContextValidatorResults.add(v.validate(permitContainer)));
        
        boolean isValid = permitContextValidatorResults.stream().allMatch(PermitValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT, PermitValidatorHelper.extractPermitViolations(permitContextValidatorResults));
        }
    }
}