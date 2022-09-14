package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsTransferredCO2MonitoringApproach {

    private String etsAccountId;

    private String approachDescription;

    private boolean deductionsToAmountExist;
    private String deductionsToAmountProcedureDescription;
    private String deductionsToAmountProcedureDocumentName;
    private String deductionsToAmountProcedureReference;
    private String deductionsToAmountDiagramReference;
    private String deductionsToAmountResponsibleDepartmentOrRole;
    private String deductionsToAmountLocationOfRecords;
    private String deductionsToAmountItSystemUsed;
    private String deductionsToAmountAppliedStandards;

    private boolean leakageEventsExist;
    private String leakageEventsProcedureDescription;
    private String leakageEventsProcedureDocumentName;
    private String leakageEventsProcedureReference;
    private String leakageEventsDiagramReference;
    private String leakageEventsResponsibleDepartmentOrRole;
    private String leakageEventsLocationOfRecords;
    private String leakageEventsItSystemUsed;
    private String leakageEventsAppliedStandards;

    private boolean temperaturePressureExist;

    private String transferOfCO2ProcedureDescription;
    private String transferOfCO2ProcedureDocumentName;
    private String transferOfCO2ProcedureReference;
    private String transferOfCO2DiagramReference;
    private String transferOfCO2ResponsibleDepartmentOrRole;
    private String transferOfCO2LocationOfRecords;
    private String transferOfCO2ItSystemUsed;
    private String transferOfCO2AppliedStandards;

    private String quantificationMethodologiesProcedureDescription;
    private String quantificationMethodologiesProcedureDocumentName;
    private String quantificationMethodologiesProcedureReference;
    private String quantificationMethodologiesDiagramReference;
    private String quantificationMethodologiesResponsibleDepartmentOrRole;
    private String quantificationMethodologiesLocationOfRecords;
    private String quantificationMethodologiesItSystemUsed;
    private String quantificationMethodologiesAppliedStandards;
}
