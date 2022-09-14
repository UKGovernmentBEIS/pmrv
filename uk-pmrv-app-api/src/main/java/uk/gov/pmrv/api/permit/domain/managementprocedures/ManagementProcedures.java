package uk.gov.pmrv.api.permit.domain.managementprocedures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#monitoringReporting != null)}", message = "permit.managementProcedures.monitoringReporting")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#assignmentOfResponsibilities != null)}", message = "permit.managementProcedures.assignmentOfResponsibilities")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#monitoringPlanAppropriateness != null)}", message = "permit.managementProcedures.monitoringPlanAppropriateness")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#dataFlowActivities != null)}", message = "permit.managementProcedures.dataFlowActivities")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#qaDataFlowActivities != null)}", message = "permit.managementProcedures.qaDataFlowActivities")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#reviewAndValidationOfData != null)}", message = "permit.managementProcedures.reviewAndValidationOfData")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#assessAndControlRisk != null)}", message = "permit.managementProcedures.assessAndControlRisk")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#qaMeteringAndMeasuringEquipment != null)}", message = "permit.managementProcedures.qaMeteringAndMeasuringEquipment")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#correctionsAndCorrectiveActions != null)}", message = "permit.managementProcedures.correctionsAndCorrectiveActions")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#controlOfOutsourcedActivities != null)}", message = "permit.managementProcedures.controlOfOutsourcedActivities")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#recordKeepingAndDocumentation != null)}", message = "permit.managementProcedures.recordKeepingAndDocumentation")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#managementProceduresExist) == (#environmentalManagementSystem != null)}", message = "permit.managementProcedures.environmentalManagementSystem")
public class ManagementProcedures implements PermitSection {

    @NotNull
    private Boolean managementProceduresExist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private MonitoringReporting monitoringReporting;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition assignmentOfResponsibilities;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition monitoringPlanAppropriateness;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private DataFlowActivities dataFlowActivities;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition qaDataFlowActivities;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition reviewAndValidationOfData;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition assessAndControlRisk;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition qaMeteringAndMeasuringEquipment;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition correctionsAndCorrectiveActions;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition controlOfOutsourcedActivities;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition recordKeepingAndDocumentation;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EnvironmentalManagementSystem environmentalManagementSystem;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        Set<UUID> attachments = new HashSet<>();
        if(dataFlowActivities != null && dataFlowActivities.getDiagramAttachmentId() != null) {
            attachments.add(dataFlowActivities.getDiagramAttachmentId());
        }

        if(monitoringReporting != null && !ObjectUtils.isEmpty(monitoringReporting.getOrganisationCharts())) {
            attachments.addAll(monitoringReporting.getOrganisationCharts());
        }

        return Collections.unmodifiableSet(attachments);
    }
}
