package uk.gov.pmrv.api.migration.files;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EtsFileAttachmentType {

    SITE_DIAGRAM("Ia_site_diagram"),
    MONITORING_REPORTING("Man_tree_diagram_or_organisational_chart-Attachments"),
    ADDITIONAL_DOCUMENTS("Ai_additional_information-Attachments"),
    MONITORING_METHODOLOGY_PLAN("Mmp_attach-Attachments"),
    SAMPLING_PLAN("Cf_procedures_analyses_sampling_plan_doc"),
    UNCERTAINTY_ANALYSIS("Calc_uncertainty_calculations_evidence");

    private String definition;
}
