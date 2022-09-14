package uk.gov.pmrv.api.permit.validation;

import static uk.gov.pmrv.api.permit.validation.PermitReferenceService.Rule.EMISSION_POINTS_USED;
import static uk.gov.pmrv.api.permit.validation.PermitReferenceService.Rule.EMISSION_SOURCES_USED;
import static uk.gov.pmrv.api.permit.validation.PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED;
import static uk.gov.pmrv.api.permit.validation.PermitReferenceService.Rule.SOURCE_STREAMS_USED;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;

@Component
@RequiredArgsConstructor
public class EmissionSummariesSectionValidator implements
    PermitSectionContextValidator<EmissionSummaries>, PermitContextValidator, PermitGrantedContextValidator {

    private final EmissionSummarySectionValidator emissionSummarySectionContextValidator;
    
    private final PermitReferenceService permitReferenceService;

    @Override
    public PermitValidationResult validate(@Valid PermitContainer permitContainer) {
        return validate(permitContainer.getPermit().getEmissionSummaries(), permitContainer);
    }

    @Override
    public PermitValidationResult validate(@Valid EmissionSummaries emissionSummariesSection, PermitContainer permitContainer) {
        if(emissionSummariesSection == null) {
            return PermitValidationResult.validPermit();
        }

        List<EmissionSummary> emissionSummaries = emissionSummariesSection.getEmissionSummaries();

        //validate that all source-streams, emission-sources, emission-points, regulated-activities are used
        List<PermitViolation> permitEmissionSummariesViolations =
                validateAllPermitElementsAreUsedInSummaries(emissionSummaries, permitContainer.getPermit());

        //for every element of the list call the validator of individual emission summary
        List<PermitValidationResult> emissionSummaryValidationResults = emissionSummaries.stream()
                .map(emissionSummary -> emissionSummarySectionContextValidator.validate(emissionSummary, permitContainer))
                .collect(Collectors.toList());

        boolean isEveryEmissionSummaryValid = emissionSummaryValidationResults.stream().allMatch(PermitValidationResult::isValid);

        if(!isEveryEmissionSummaryValid) {
            emissionSummaryValidationResults.stream()
                    .filter(permitValidationResult -> !permitValidationResult.isValid())
                    .forEach(permitValidationResult -> permitEmissionSummariesViolations.addAll(permitValidationResult.getPermitViolations()));
        }

        return PermitValidationResult.builder()
                .valid(permitEmissionSummariesViolations.isEmpty() && isEveryEmissionSummaryValid)
                .permitViolations(permitEmissionSummariesViolations)
                .build();
    }

    private List<PermitViolation> validateAllPermitElementsAreUsedInSummaries(List<EmissionSummary> emissionSummaries, Permit permit) {
        return Stream.of(
                
                // validate source streams
                permitReferenceService.validateAllInPermitAreUsed(
                    permit.getSourceStreamsIdRefMap(),
                    emissionSummaries.stream().map(EmissionSummary::getSourceStream).filter(Objects::nonNull)
                        .collect(Collectors.toSet()),
                    SOURCE_STREAMS_USED),

                // validate emission sources
                permitReferenceService.validateAllInPermitAreUsed(
                    permit.getEmissionSourcesIdRefMap(),
                    emissionSummaries.stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream())
                        .filter(Objects::nonNull).collect(Collectors.toSet()),
                    EMISSION_SOURCES_USED),

                // validate emission points
                permitReferenceService.validateAllInPermitAreUsed(
                    permit.getEmissionPointsIdRefMap(),
                    emissionSummaries.stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream())
                        .filter(Objects::nonNull).collect(Collectors.toSet()),
                    EMISSION_POINTS_USED),
                
                // validate regulated activities
                permitReferenceService.validateAllInPermitAreUsed(
                    permit.getRegulatedActivitiesIdTypeMap(),
                    emissionSummaries.stream().map(EmissionSummary::getRegulatedActivity)
                        .filter(Objects::nonNull).collect(Collectors.toSet()),
                    REGULATED_ACTIVITIES_USED)
            
            ).filter(Optional::isPresent).flatMap(Optional::stream)
            .map(p -> {
                final Object[] data = p.getRight() != null ? p.getRight().entrySet().toArray() : new Object[] {};
                return new PermitViolation(EmissionSummaries.class.getSimpleName(),
                                           p.getLeft(),
                                           data);
            })
            .collect(Collectors.toList());
    }
}
