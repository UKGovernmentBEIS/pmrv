package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import lombok.experimental.UtilityClass;

import uk.gov.pmrv.api.migration.permit.measurementdevices.MeasurementDeviceTypeMapper;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.MeasurementDevice;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.ReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.ReceivingTransferringInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TemperaturePressure;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class TransferredCO2ApproachDetailsMapper {

    public Map<String, TransferredCO2MonitoringApproach> transformToTransferredCO2MonitoringApproach(Map<String, EtsTransferredCO2MonitoringApproach> etsTransferredCO2MonitoringApproachesMap,
                                                                                        List<EtsReceivingTransferringInstallation> etsReceivingTransferringInstallations,
                                                                                        List<EtsTemperaturePressure> etsTemperaturePressures) {
        Map<String, TransferredCO2MonitoringApproach> transferredCO2MonitoringApproachesMap = new HashMap<>();
        etsTransferredCO2MonitoringApproachesMap.forEach((accountId, approach) -> {
            TransferredCO2MonitoringApproach transferredCO2Approach = TransferredCO2MonitoringApproach.builder()
                    .receivingTransferringInstallations(transformToInstallation(etsReceivingTransferringInstallations, accountId))
                    .deductionsToAmountOfTransferredCO2(transformToDeductions(approach))
                    .procedureForLeakageEvents(transformToLeakage(approach))
                    .temperaturePressure(transformToTemperature(approach, etsTemperaturePressures, accountId))
                    .transferOfCO2(transformToTransfer(approach))
                    .quantificationMethodologies(transformToQuantification(approach))
                    .approachDescription(approach.getApproachDescription())
                    .build();
            transferredCO2MonitoringApproachesMap.put(accountId, transferredCO2Approach);
        });

        return transferredCO2MonitoringApproachesMap;
    }

    private List<ReceivingTransferringInstallation> transformToInstallation(List<EtsReceivingTransferringInstallation> etsReceivingTransferringInstallations,
                                                                            String accountId) {
        return etsReceivingTransferringInstallations.stream()
                .filter(installation -> installation.getEtsAccountId().equals(accountId))
                .map(installation -> ReceivingTransferringInstallation.builder()
                        .type(installation.getType().contains("Transferring")
                                ? ReceivingTransferringInstallationType.TRANSFERRING
                                : ReceivingTransferringInstallationType.RECEIVING)
                        .installationIdentificationCode(installation.getInstallationIdentificationCode())
                        .operator(installation.getOperator())
                        .installationName(installation.getInstallationName())
                        .co2source(installation.getCo2source())
                        .build())
                .collect(Collectors.toList());
    }

    private ProcedureOptionalForm transformToDeductions(EtsTransferredCO2MonitoringApproach etsApproach) {
        return etsApproach.isDeductionsToAmountExist()
                ? ProcedureOptionalForm.builder()
                    .procedureForm(ProcedureForm.builder()
                            .procedureDescription(etsApproach.getDeductionsToAmountProcedureDescription())
                            .procedureDocumentName(etsApproach.getDeductionsToAmountProcedureDocumentName())
                            .procedureReference(etsApproach.getDeductionsToAmountProcedureReference())
                            .diagramReference(etsApproach.getDeductionsToAmountDiagramReference())
                            .responsibleDepartmentOrRole(etsApproach.getDeductionsToAmountResponsibleDepartmentOrRole())
                            .locationOfRecords(etsApproach.getDeductionsToAmountLocationOfRecords())
                            .itSystemUsed(etsApproach.getDeductionsToAmountItSystemUsed())
                            .appliedStandards(etsApproach.getDeductionsToAmountAppliedStandards())
                            .build())
                    .build()
                : ProcedureOptionalForm.builder().exist(false).build();
    }

    private ProcedureOptionalForm transformToLeakage(EtsTransferredCO2MonitoringApproach etsApproach) {
        return etsApproach.isLeakageEventsExist()
                ? ProcedureOptionalForm.builder()
                .procedureForm(ProcedureForm.builder()
                        .procedureDescription(etsApproach.getLeakageEventsProcedureDescription())
                        .procedureDocumentName(etsApproach.getLeakageEventsProcedureDocumentName())
                        .procedureReference(etsApproach.getLeakageEventsProcedureReference())
                        .diagramReference(etsApproach.getLeakageEventsDiagramReference())
                        .responsibleDepartmentOrRole(etsApproach.getLeakageEventsResponsibleDepartmentOrRole())
                        .locationOfRecords(etsApproach.getLeakageEventsLocationOfRecords())
                        .itSystemUsed(etsApproach.getLeakageEventsItSystemUsed())
                        .appliedStandards(etsApproach.getLeakageEventsAppliedStandards())
                        .build())
                .build()
                : ProcedureOptionalForm.builder().exist(false).build();
    }

    private TemperaturePressure transformToTemperature(EtsTransferredCO2MonitoringApproach etsApproach,
                                                       List<EtsTemperaturePressure> etsTemperaturePressures,
                                                       String accountId) {
        if(etsApproach.isTemperaturePressureExist()) {
            List<MeasurementDevice> measurementDevices = etsTemperaturePressures.stream()
                    .filter(temperature -> temperature.getEtsAccountId().equals(accountId))
                    .map(temperature -> {
                        MeasurementDeviceType type = MeasurementDeviceTypeMapper.getMeasurementDeviceType(temperature.getType());
                        return MeasurementDevice.builder()
                                .reference(temperature.getReference())
                                .type(type)
                                .otherTypeName(type.equals(MeasurementDeviceType.OTHER) ? temperature.getType() : null)
                                .location(temperature.getLocation())
                                .build();
                    })
                    .collect(Collectors.toList());

            return TemperaturePressure.builder().exist(true).measurementDevices(measurementDevices).build();
        }

        return TemperaturePressure.builder().exist(false).build();
    }

    private ProcedureForm transformToTransfer(EtsTransferredCO2MonitoringApproach etsApproach) {
        return ProcedureForm.builder()
                .procedureDescription(etsApproach.getTransferOfCO2ProcedureDescription())
                .procedureDocumentName(etsApproach.getTransferOfCO2ProcedureDocumentName())
                .procedureReference(etsApproach.getTransferOfCO2ProcedureReference())
                .diagramReference(etsApproach.getTransferOfCO2DiagramReference())
                .responsibleDepartmentOrRole(etsApproach.getTransferOfCO2ResponsibleDepartmentOrRole())
                .locationOfRecords(etsApproach.getTransferOfCO2LocationOfRecords())
                .itSystemUsed(etsApproach.getTransferOfCO2ItSystemUsed())
                .appliedStandards(etsApproach.getTransferOfCO2AppliedStandards())
                .build();
    }

    private ProcedureForm transformToQuantification(EtsTransferredCO2MonitoringApproach etsApproach) {
        return ProcedureForm.builder()
                .procedureDescription(etsApproach.getQuantificationMethodologiesProcedureDescription())
                .procedureDocumentName(etsApproach.getQuantificationMethodologiesProcedureDocumentName())
                .procedureReference(etsApproach.getQuantificationMethodologiesProcedureReference())
                .diagramReference(etsApproach.getQuantificationMethodologiesDiagramReference())
                .responsibleDepartmentOrRole(etsApproach.getQuantificationMethodologiesResponsibleDepartmentOrRole())
                .locationOfRecords(etsApproach.getQuantificationMethodologiesLocationOfRecords())
                .itSystemUsed(etsApproach.getQuantificationMethodologiesItSystemUsed())
                .appliedStandards(etsApproach.getQuantificationMethodologiesAppliedStandards())
                .build();
    }
}
