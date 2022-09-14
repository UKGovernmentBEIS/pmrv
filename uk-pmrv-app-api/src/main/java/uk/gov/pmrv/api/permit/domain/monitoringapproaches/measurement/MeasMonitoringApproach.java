package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MeasMonitoringApproach extends PermitMonitoringApproachSection {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;

    @Valid
    @NotNull
    private ProcedureForm emissionDetermination;

    @Valid
    @NotNull
    private ProcedureForm referencePeriodDetermination;

    @Valid
    @NotNull
    private ProcedureOptionalForm gasFlowCalculation;

    @Valid
    @NotNull
    private ProcedureOptionalForm biomassEmissions;

    @Valid
    @NotNull
    private ProcedureForm corroboratingCalculations;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<MeasSourceStreamCategoryAppliedTier> sourceStreamCategoryAppliedTiers = new ArrayList<>();
    
    @Override
    public Set<UUID> getAttachmentIds() {

        Set<UUID> attachments = new HashSet<>(sourceStreamCategoryAppliedTiers.stream()
                .map(MeasSourceStreamCategoryAppliedTier::getMeasuredEmissions)
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
            .map(MeasSourceStreamCategoryAppliedTier::getSourceStreamCategory)
            .filter(Objects::nonNull)
            .map(MeasSourceStreamCategory::getEmissionPoints)
            .filter(Objects::nonNull)
            .flatMap(Set::stream)
            .collect(Collectors.toSet()));

        return Collections.unmodifiableSet(emissionPoints);
    }
}
