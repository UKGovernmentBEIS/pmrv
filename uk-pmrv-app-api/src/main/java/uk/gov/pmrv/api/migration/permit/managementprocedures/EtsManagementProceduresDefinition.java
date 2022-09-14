package uk.gov.pmrv.api.migration.permit.managementprocedures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EtsManagementProceduresDefinition {

    private String emitterId;

    private String procedureTitle;

    private String procedureReference;

    private String diagramReference;

    private String procedureDescription;

    private String responsiblePostOrDepartment;

    private String procedureLocation;

    private String procedureItSystem;

    private String enOrOtherStandardsApplied;
}
