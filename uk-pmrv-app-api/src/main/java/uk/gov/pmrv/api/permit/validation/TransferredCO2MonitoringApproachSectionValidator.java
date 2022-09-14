package uk.gov.pmrv.api.permit.validation;

import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.ACCOUNTING_EMISSIONS_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions.AccountingEmissions;

@Component
@RequiredArgsConstructor
public class TransferredCO2MonitoringApproachSectionValidator
        implements PermitSectionContextValidator<TransferredCO2MonitoringApproach>, PermitContextValidator, PermitGrantedContextValidator {

    private final PermitReferenceService permitReferenceService;

    @Override
    public PermitValidationResult validate(PermitContainer permitContainer) {
        final Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
                permitContainer.getPermit().getMonitoringApproaches().getMonitoringApproaches();

        if (monitoringApproaches.containsKey(MonitoringApproachType.TRANSFERRED_CO2)) {
            return validate((TransferredCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.TRANSFERRED_CO2),
                    permitContainer);
        }
        return PermitValidationResult.validPermit();
    }

    @Override
    public PermitValidationResult validate(TransferredCO2MonitoringApproach permitSection, PermitContainer permitContainer) {
        if (ObjectUtils.isEmpty(permitSection) || ObjectUtils.isEmpty(permitSection.getAccountingEmissions())) {
            return PermitValidationResult.validPermit();
        }

        AtomicReference<List<PermitViolation>> permitViolations = new AtomicReference<>();
        
        if(!permitSection.getAccountingEmissions().isChemicallyBound()){
            Optional.ofNullable(permitSection.getAccountingEmissions().getAccountingEmissionsDetails())
                    .ifPresentOrElse(accountingEmissionsOptional ->
                            permitViolations.set(Stream.of(
                                    // Validate measurement devices or methods
                                    permitReferenceService.validateExistenceInPermit(
                                            permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                                            permitSection.getAccountingEmissions().getAccountingEmissionsDetails().getMeasurementDevicesOrMethods(),
                                            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST)
                            ).filter(Optional::isPresent).map(p -> {
                                final Object[] data = p.get().getRight() != null ? p.get().getRight().toArray() : new Object[] {};
                                return new PermitViolation(AccountingEmissions.class.getSimpleName(),
                                        p.get().getLeft(),
                                        data);
                            }).collect(Collectors.toList()))
            , () -> permitViolations.set(List.of(new PermitViolation(AccountingEmissions.class.getSimpleName(), ACCOUNTING_EMISSIONS_NOT_FOUND))));
        }

        return PermitValidationResult.builder()
                .valid(ObjectUtils.isEmpty(permitViolations.get()))
                .permitViolations(ObjectUtils.isEmpty(permitViolations.get()) ? new ArrayList<>() : permitViolations.get())
                .build();
    }

}
