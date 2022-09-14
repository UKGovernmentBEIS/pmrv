package uk.gov.pmrv.api.migration.permit.managementprocedures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EtsDataFlowActivities extends EtsManagementProceduresDefinition {

    private String primaryDataSources;

    private String relevantProcessingSteps;
}
