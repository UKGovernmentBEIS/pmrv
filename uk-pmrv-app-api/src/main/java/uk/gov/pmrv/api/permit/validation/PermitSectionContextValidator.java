package uk.gov.pmrv.api.permit.validation;

import javax.validation.Valid;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitSection;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;

public interface PermitSectionContextValidator<T extends PermitSection> {

    PermitValidationResult validate(@Valid T permitSection, PermitContainer permitContainer);
    
}
