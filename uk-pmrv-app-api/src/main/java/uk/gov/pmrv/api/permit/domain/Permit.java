package uk.gov.pmrv.api.permit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.domain.installationdesc.InstallationDescription;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.sitediagram.SiteDiagrams;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.permit.domain.uncertaintyanalysis.UncertaintyAnalysis;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permit {

    @Valid
    @NotNull
    private EnvironmentalPermitsAndLicences environmentalPermitsAndLicences;

    @Valid
    @NotNull
    private EstimatedAnnualEmissions estimatedAnnualEmissions;

    @Valid
    @NotNull
    private InstallationDescription installationDescription;
    
    @JsonUnwrapped
    @Valid
    @NotNull
    private RegulatedActivities regulatedActivities;

    @Valid
    @NotNull
    private MonitoringMethodologyPlans monitoringMethodologyPlans;
    
    @JsonUnwrapped
    @Valid
    @NotNull
    private SourceStreams sourceStreams;

    @JsonUnwrapped
    @Valid
    @NotNull
    private MeasurementDevicesOrMethods measurementDevicesOrMethods;

    @JsonUnwrapped
    @Valid
    @NotNull
    private EmissionSources emissionSources;

    @JsonUnwrapped
    @Valid
    @NotNull
    private EmissionPoints emissionPoints;

    @JsonUnwrapped
    @Valid
    @NotNull
    private EmissionSummaries emissionSummaries;

    @JsonUnwrapped
    @Valid
    private SiteDiagrams siteDiagrams;

    @Valid
    @NotNull
    private Abbreviations abbreviations;

    @Valid
    @NotNull
    private ConfidentialityStatement confidentialityStatement;

    @Valid
    @NotNull
    private AdditionalDocuments additionalDocuments;

    @JsonUnwrapped
    @Valid
    @NotNull
    private MonitoringApproaches monitoringApproaches;

    @Valid
    @NotNull
    private UncertaintyAnalysis uncertaintyAnalysis;

    @JsonUnwrapped
    @Valid
    @NotNull
    private ManagementProcedures managementProcedures;
    
    @JsonIgnore
    public Set<UUID> getPermitSectionAttachmentIds(){
        Set<UUID> attachments = new HashSet<>();
        if(monitoringMethodologyPlans != null && !ObjectUtils.isEmpty(monitoringMethodologyPlans.getPlans())) {
            attachments.addAll(monitoringMethodologyPlans.getPlans());
        }
        
        if(siteDiagrams != null && !ObjectUtils.isEmpty(siteDiagrams.getSiteDiagrams())) {
            attachments.addAll(siteDiagrams.getSiteDiagrams());
        }

        if(additionalDocuments != null && !ObjectUtils.isEmpty(additionalDocuments.getDocuments())) {
            attachments.addAll(additionalDocuments.getDocuments());
        }
        
        if(monitoringApproaches != null) {
            attachments.addAll(monitoringApproaches.getAttachmentIds());
        }

        if(uncertaintyAnalysis != null && !ObjectUtils.isEmpty(uncertaintyAnalysis.getAttachments())) {
            attachments.addAll(uncertaintyAnalysis.getAttachments());
        }

        if(managementProcedures != null) {
            attachments.addAll(managementProcedures.getAttachmentIds());
        }
        
        return Collections.unmodifiableSet(attachments);
    }

    @JsonIgnore
    public Set<String> getSourceStreamsIds() {

        return this.getSourceStreams().getSourceStreams().stream()
            .map(SourceStream::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> getEmissionSourcesIds() {

        return this.getEmissionSources().getEmissionSources().stream()
            .map(EmissionSource::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> getEmissionPointsIds() {

        return this.getEmissionPoints().getEmissionPoints().stream()
            .map(EmissionPoint::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> getMeasurementDevicesOrMethodsIds() {

        return this.getMeasurementDevicesOrMethods().getMeasurementDevicesOrMethods().stream()
            .map(MeasurementDeviceOrMethod::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> getRegulatedActivitiesIds() {

        return this.getRegulatedActivities().getRegulatedActivities().stream()
            .map(RegulatedActivity::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Map<String, String> getSourceStreamsIdRefMap() {

        return this.getSourceStreams().getSourceStreams().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(SourceStream::getId, SourceStream::getReference));
    }

    @JsonIgnore
    public Map<String, String> getEmissionSourcesIdRefMap() {

        return this.getEmissionSources().getEmissionSources().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(EmissionSource::getId, EmissionSource::getReference));
    }

    @JsonIgnore
    public Map<String, String> getEmissionPointsIdRefMap() {

        return this.getEmissionPoints().getEmissionPoints().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(EmissionPoint::getId, EmissionPoint::getReference));
    }
    
    @JsonIgnore
    public Map<String, String> getRegulatedActivitiesIdTypeMap() {

        return this.getRegulatedActivities().getRegulatedActivities().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(RegulatedActivity::getId,
                regulatedActivity -> regulatedActivity.getType().toString()));
    }
}
