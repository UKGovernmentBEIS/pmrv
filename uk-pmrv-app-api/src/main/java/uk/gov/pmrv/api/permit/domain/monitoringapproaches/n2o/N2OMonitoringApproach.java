package uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class N2OMonitoringApproach extends PermitMonitoringApproachSection {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;

    @Valid
    @NotNull
    private ProcedureForm emissionDetermination;

    @Valid
    @NotNull
    private ProcedureForm referenceDetermination;

    @Valid
    @NotNull
    private ProcedureForm operationalManagement;

    @Valid
    @NotNull
    private ProcedureForm nitrousOxideEmissionsDetermination;

    @Valid
    @NotNull
    private ProcedureForm nitrousOxideConcentrationDetermination;

    @Valid
    @NotNull
    private ProcedureForm quantityProductDetermination;

    @Valid
    @NotNull
    private ProcedureForm quantityMaterials;

    @Valid
    @NotNull
    private ProcedureOptionalForm gasFlowCalculation;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<N2OSourceStreamCategoryAppliedTier> sourceStreamCategoryAppliedTiers = new ArrayList<>();
    
    @Override
    public Set<UUID> getAttachmentIds() {

        Set<UUID> attachments = new HashSet<>(sourceStreamCategoryAppliedTiers.stream()
                .map(N2OSourceStreamCategoryAppliedTier::getMeasuredEmissions)
                .filter(Objects::nonNull)
                .map(MeasuredEmissions::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        return Collections.unmodifiableSet(attachments);
    }

    @JsonIgnore
    public Set<String> getEmissionPoints() {
        Set<String> emissionPoints = new HashSet<>(sourceStreamCategoryAppliedTiers.stream()
            .map(N2OSourceStreamCategoryAppliedTier::getSourceStreamCategory)
            .filter(Objects::nonNull)
            .map(N2OSourceStreamCategory::getEmissionPoints)
            .filter(Objects::nonNull)
            .flatMap(Set::stream)
            .collect(Collectors.toSet()));

        return Collections.unmodifiableSet(emissionPoints);
    }
}
