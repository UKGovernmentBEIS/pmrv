package uk.gov.pmrv.api.permit.validation;

import javax.validation.Valid;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;

public interface PermitContextValidator {

    PermitValidationResult validate(@Valid PermitContainer permitContainer);
    
}
