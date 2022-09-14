package uk.gov.pmrv.api.permit.validation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;

@Component
@RequiredArgsConstructor
public class CalculationMonitoringApproachSectionValidator 
    implements PermitSectionContextValidator<CalculationMonitoringApproach>, PermitContextValidator, PermitGrantedContextValidator {
    
    private final PermitReferenceService permitReferenceService;

    @Override
    public PermitValidationResult validate(@Valid PermitContainer permitContainer) {
        MonitoringApproachType type = MonitoringApproachType.CALCULATION;
        
        return permitContainer.getPermit().getMonitoringApproaches().getMonitoringApproaches().containsKey(type)
                ? validate((CalculationMonitoringApproach) permitContainer.getPermit().getMonitoringApproaches()
                        .getMonitoringApproaches().get(type), permitContainer)
                : PermitValidationResult.validPermit();
    }

    @Override
    public PermitValidationResult validate(final @Valid CalculationMonitoringApproach permitSection,
            PermitContainer permitContainer) {
        if (ObjectUtils.isEmpty(permitSection)) {
            return PermitValidationResult.validPermit();
        }
        final Permit permit = permitContainer.getPermit();

        List<PermitViolation> permitViolations = permitSection.getSourceStreamCategoryAppliedTiers().stream()
            .map(appliedTier -> Stream.of(

                // validate source stream
                    permitReferenceService.validateExistenceInPermit(
                    permit.getSourceStreamsIds(),
                    List.of(appliedTier.getSourceStreamCategory().getSourceStream()),
                    PermitReferenceService.Rule.SOURCE_STREAM_EXISTS),

                // validate emission sources
                    permitReferenceService.validateExistenceInPermit(
                    permit.getEmissionSourcesIds(),
                    appliedTier.getSourceStreamCategory().getEmissionSources(),
                    PermitReferenceService.Rule.EMISSION_SOURCES_EXIST),
                
                // validate measurement devices or methods in activity data
                permitReferenceService.validateExistenceInPermit(
                    permit.getMeasurementDevicesOrMethodsIds(),
                    appliedTier.getActivityData().getMeasurementDevicesOrMethods(),
                    PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST)

                ).filter(Optional::isPresent).flatMap(Optional::stream).collect(Collectors.toList())
            ).flatMap(List::stream).map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[] {};
                return new PermitViolation(CalculationMonitoringApproach.class.getSimpleName(),
                    p.getLeft(),
                    data);
            }).collect(Collectors.toList());

        return PermitValidationResult.builder()
            .valid(permitViolations.isEmpty())
            .permitViolations(permitViolations)
            .build();
    }

}
