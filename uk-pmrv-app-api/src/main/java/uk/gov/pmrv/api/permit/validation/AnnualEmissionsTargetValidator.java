package uk.gov.pmrv.api.permit.validation;

import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.ANNUAL_EMISSIONS_TARGET_INVALID_PRECISION;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.ANNUAL_EMISSIONS_TARGET_INVALID_YEAR;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.ANNUAL_EMISSIONS_TARGET_NOT_FOUND;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_ANNUAL_EMISSIONS_TARGET;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;

@Component
public class AnnualEmissionsTargetValidator implements PermitGrantedContextValidator {

    @Override
    public PermitValidationResult validate(@Valid PermitContainer permitContainer) {
        List<PermitViolation> permitViolations = new ArrayList<>();
        SortedMap<String, BigDecimal> annualEmissionsTargets = permitContainer.getAnnualEmissionsTargets();

        if(PermitType.HSE.equals(permitContainer.getPermitType())) {
            if(annualEmissionsTargets.isEmpty()) {
                permitViolations.add(new PermitViolation(ANNUAL_EMISSIONS_TARGET_NOT_FOUND));
            }

            //check that all years defined for annual emissions target consist of 4 digits with no leading zeros
            List<String> annualEmissionsTargetsWithInvalidYear = annualEmissionsTargets.keySet().stream()
                .filter(key -> !key.matches("((?!(0))[0-9]{4})"))
                .collect(Collectors.toList());

            if(!annualEmissionsTargetsWithInvalidYear.isEmpty()) {
                permitViolations.add(new PermitViolation(ANNUAL_EMISSIONS_TARGET_INVALID_YEAR));
            }

            List<BigDecimal> annualEmissionsTargetsWithInvalidPrecision = annualEmissionsTargets.values().stream()
                .filter(target -> target.scale() > 1)
                .collect(Collectors.toList());

            if(!annualEmissionsTargetsWithInvalidPrecision.isEmpty()) {
                permitViolations.add(new PermitViolation(ANNUAL_EMISSIONS_TARGET_INVALID_PRECISION));
            }
        }
        //permit type is GHGE
        else {
            if (!annualEmissionsTargets.isEmpty()) {
                permitViolations.add(new PermitViolation(INVALID_ANNUAL_EMISSIONS_TARGET));
            }
        }

        return PermitValidationResult.builder()
            .valid(permitViolations.isEmpty())
            .permitViolations(permitViolations)
            .build();
    }
}
