package uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc;

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

import org.springframework.util.ObjectUtils;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PFCMonitoringApproach extends PermitMonitoringApproachSection {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<CellAndAnodeType> cellAndAnodeTypes = new ArrayList<>();

    @Valid
    @NotNull
    private ProcedureForm collectionEfficiency;

    @Valid
    @NotNull
    private PFCTier2EmissionFactor tier2EmissionFactor;
    
    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<PFCSourceStreamCategoryAppliedTier> sourceStreamCategoryAppliedTiers = new ArrayList<>();
    
    @Override
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(PFCSourceStreamCategoryAppliedTier::getActivityData)
                .filter(Objects::nonNull)
                .map(PFCActivityData::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        attachments.addAll(sourceStreamCategoryAppliedTiers.stream()
                .map(PFCSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .map(PFCEmissionFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        
        return Collections.unmodifiableSet(attachments);
    }
}
