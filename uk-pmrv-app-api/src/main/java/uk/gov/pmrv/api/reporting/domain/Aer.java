package uk.gov.pmrv.api.reporting.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.nace.NaceCodes;
import uk.gov.pmrv.api.reporting.domain.pollutantregistercodes.PollutantRegisterActivities;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivities;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#monitoringApproachTypes.contains('MEASUREMENT') || #monitoringApproachTypes.contains('N2O')) == (#emissionPoints != null)}",
    message = "aer.monitoringApproachTypes.emissionPoints")
public class Aer {

    @Valid
    @NotNull
    private AdditionalDocuments additionalDocuments;

    @Valid
    @NotNull
    private Abbreviations abbreviations;

    @Valid
    @NotNull
    private ConfidentialityStatement confidentialityStatement;

    @Valid
    @NotNull
    private PollutantRegisterActivities pollutantRegisterActivities;

    @Valid
    @NotNull
    private NaceCodes naceCodes;

    @JsonUnwrapped
    @Valid
    @NotNull
    private SourceStreams sourceStreams;

    @JsonUnwrapped
    @Valid
    @NotNull
    private EmissionSources emissionSources;

    @JsonUnwrapped
    @Valid
    private EmissionPoints emissionPoints;

    @NotEmpty
    private Set<MonitoringApproachType> monitoringApproachTypes = new HashSet<>();
    
    @JsonUnwrapped
    @Valid
    @NotNull
    private AerRegulatedActivities regulatedActivities;


    @JsonIgnore
    public Set<UUID> getAerSectionAttachmentIds(){
        Set<UUID> attachments = new HashSet<>();

        if(additionalDocuments != null && !ObjectUtils.isEmpty(additionalDocuments.getDocuments())) {
            attachments.addAll(additionalDocuments.getDocuments());
        }

        return Collections.unmodifiableSet(attachments);
    }
}
