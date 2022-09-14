package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CalculationMonitoringApproach extends PermitMonitoringApproachSection {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;
    
    @Valid
    @NotNull
    private SamplingPlan samplingPlan;
    
    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<CalculationSourceStreamCategoryAppliedTier> sourceStreamCategoryAppliedTiers = new ArrayList<>();

    @Override
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();
        
        if(samplingPlan != null 
                && samplingPlan.getDetails() != null 
                && samplingPlan.getDetails().getProcedurePlan() != null) {
            attachments.addAll(samplingPlan.getDetails().getProcedurePlan().getProcedurePlanIds());
        }
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .map(CalculationEmissionFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .flatMap(ef -> ef.getOneThirdRuleFiles().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .flatMap(factor -> factor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .flatMap(factor -> factor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getActivityData)
                .filter(Objects::nonNull)
                .map(CalculationActivityData::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getOxidationFactor)
                .filter(Objects::nonNull)
                .map(CalculationOxidationFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getOxidationFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationOxidationFactor -> calculationOxidationFactor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getOxidationFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationOxidationFactor -> calculationOxidationFactor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getCarbonContent)
                .filter(Objects::nonNull)
                .map(CalculationCarbonContent::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getCarbonContent)
                .filter(Objects::nonNull)
                .flatMap(carbon -> carbon.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getCarbonContent)
                .filter(Objects::nonNull)
                .flatMap(content -> content.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getNetCalorificValue)
                .filter(Objects::nonNull)
                .map(CalculationNetCalorificValue::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getNetCalorificValue)
                .filter(Objects::nonNull)
                .flatMap(calculationNetCalorificValue -> calculationNetCalorificValue.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getNetCalorificValue)
                .filter(Objects::nonNull)
                .flatMap(calculationNetCalorificValue -> calculationNetCalorificValue.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getConversionFactor)
                .filter(Objects::nonNull)
                .map(CalculationConversionFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getConversionFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationConversionFactor -> calculationConversionFactor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getConversionFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationConversionFactor -> calculationConversionFactor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getBiomassFraction)
                .filter(Objects::nonNull)
                .map(CalculationBiomassFraction::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getBiomassFraction)
                .filter(Objects::nonNull)
                .flatMap(calculationBiomassFraction -> calculationBiomassFraction.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getBiomassFraction)
                .filter(Objects::nonNull)
                .flatMap(calculationBiomassFraction -> calculationBiomassFraction.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

        return Collections.unmodifiableSet(attachments);
    }
}
