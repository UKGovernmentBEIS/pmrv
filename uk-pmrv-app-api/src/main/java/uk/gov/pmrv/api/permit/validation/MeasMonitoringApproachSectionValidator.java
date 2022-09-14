package uk.gov.pmrv.api.permit.validation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;

@Component
@RequiredArgsConstructor
public class MeasMonitoringApproachSectionValidator
    implements PermitSectionContextValidator<MeasMonitoringApproach>, PermitContextValidator, PermitGrantedContextValidator {

    private final PermitReferenceService permitReferenceService;

    @Override
    public PermitValidationResult validate(final PermitContainer permitContainer) {

        final Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            permitContainer.getPermit().getMonitoringApproaches().getMonitoringApproaches();

        if (monitoringApproaches.containsKey(MonitoringApproachType.MEASUREMENT)) {
            return validate((MeasMonitoringApproach) monitoringApproaches.get(MonitoringApproachType.MEASUREMENT),
                permitContainer);
        }
        return PermitValidationResult.validPermit();
    }

    @Override
    public PermitValidationResult validate(final MeasMonitoringApproach permitSection,
                                           final PermitContainer permitContainer) {

        if (ObjectUtils.isEmpty(permitSection)) {
            return PermitValidationResult.validPermit();
        }
        final Permit permit = permitContainer.getPermit();

        final List<PermitViolation> permitViolations = permitSection.getSourceStreamCategoryAppliedTiers().stream()
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

                // validate emission points
                permitReferenceService.validateExistenceInPermit(
                    permit.getEmissionPointsIds(),
                    appliedTier.getSourceStreamCategory().getEmissionPoints(),
                    PermitReferenceService.Rule.EMISSION_POINTS_EXIST),

                // validate measurement devices or methods
                permitReferenceService.validateExistenceInPermit(
                    permit.getMeasurementDevicesOrMethodsIds(),
                    appliedTier.getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                    PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST)
                ).filter(Optional::isPresent).flatMap(Optional::stream).collect(Collectors.toList())
            ).flatMap(List::stream).map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[] {};
                return new PermitViolation(MeasMonitoringApproach.class.getSimpleName(),
                                           p.getLeft(),
                                           data);
            }).collect(Collectors.toList());

        return PermitValidationResult.builder()
            .valid(permitViolations.isEmpty())
            .permitViolations(permitViolations)
            .build();
    }

}