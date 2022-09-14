package uk.gov.pmrv.api.reporting.validation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.reporting.domain.AerContainer;

@Service
@Validated
public class AerValidatorService {

    public void validate(@Valid @NotNull AerContainer aerContainer) {};
}
