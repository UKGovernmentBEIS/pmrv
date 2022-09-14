package uk.gov.pmrv.api.permit.validation;

import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.EMISSION_SUMMARY_INVALID_EXCLUDED_ACTIVITY;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;

@Component
@RequiredArgsConstructor
public class EmissionSummarySectionValidator implements PermitSectionContextValidator<EmissionSummary> {

    private final PermitReferenceService permitReferenceService;
    
    
    @Override
    public PermitValidationResult validate(@Valid EmissionSummary emissionSummarySection, PermitContainer permitContainer) {
        if(emissionSummarySection == null) {
            return PermitValidationResult.validPermit();
        }
        final Permit permit = permitContainer.getPermit();
        
        List<PermitViolation> permitViolations =
            Stream.of(

                    // validate source stream
                    permitReferenceService.validateExistenceInPermit(
                        permit.getSourceStreamsIds(),
                        List.of(emissionSummarySection.getSourceStream()),
                        PermitReferenceService.Rule.SOURCE_STREAM_EXISTS),

                    // validate emission sources
                    permitReferenceService.validateExistenceInPermit(
                        permit.getEmissionSourcesIds(),
                        emissionSummarySection.getEmissionSources(),
                        PermitReferenceService.Rule.EMISSION_SOURCES_EXIST),

                    // validate emission points
                    permitReferenceService.validateExistenceInPermit(
                        permit.getEmissionPointsIds(),
                        emissionSummarySection.getEmissionPoints(),
                        PermitReferenceService.Rule.EMISSION_POINTS_EXIST),

                    this.validateRegulatedActivity(permit,
                        emissionSummarySection.getRegulatedActivity(),
                        emissionSummarySection.isExcludedRegulatedActivity())

                ).filter(Optional::isPresent).flatMap(Optional::stream)
                .map(p -> {
                    final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[] {};
                    return new PermitViolation(EmissionSummary.class.getSimpleName(),
                                               p.getLeft(),
                                               data);
                })
                .collect(Collectors.toList());

        return PermitValidationResult.builder()
                .valid(permitViolations.isEmpty())
                .permitViolations(permitViolations)
                .build();
    }

    private Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> validateRegulatedActivity(
        final Permit permit, 
        final String regulatedActivity, 
        final boolean isExcludedRegulatedActivity) {
        
        if(isExcludedRegulatedActivity && regulatedActivity != null) {
            return Optional.of(Pair.of(EMISSION_SUMMARY_INVALID_EXCLUDED_ACTIVITY, Collections.emptyList()));
        }

        if(!isExcludedRegulatedActivity) {
            return permitReferenceService.validateExistenceInPermit(permit.getRegulatedActivitiesIds(),
                                                                List.of(regulatedActivity),
                                                                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS);
        }

        return Optional.empty();
    }
}
