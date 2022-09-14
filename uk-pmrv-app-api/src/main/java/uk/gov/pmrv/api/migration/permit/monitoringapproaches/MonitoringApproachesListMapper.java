package uk.gov.pmrv.api.migration.permit.monitoringapproaches;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;

import java.util.EnumMap;
import java.util.Map;

@Log4j2
@UtilityClass
public final class MonitoringApproachesListMapper {

    public static MonitoringApproaches constructMonitoringApproaches(EtsMonitoringApproachesList etsMonitoringApproachesList) {
        Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = new EnumMap<>(MonitoringApproachType.class);
        if (etsMonitoringApproachesList.isCalculation()) {
            monitoringApproaches.put(MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().type(MonitoringApproachType.CALCULATION).build());
        }
        if (etsMonitoringApproachesList.isFallback()) {
            monitoringApproaches.put(MonitoringApproachType.FALLBACK, FallbackMonitoringApproach.builder().type(MonitoringApproachType.FALLBACK).build());
        }
        if (etsMonitoringApproachesList.isMeasurement()) {
            monitoringApproaches.put(MonitoringApproachType.MEASUREMENT, MeasMonitoringApproach.builder().type(MonitoringApproachType.MEASUREMENT).build());
        }
        if (etsMonitoringApproachesList.isPfc()) {
            monitoringApproaches.put(MonitoringApproachType.PFC, PFCMonitoringApproach.builder().type(MonitoringApproachType.PFC).build());
        }
        if (etsMonitoringApproachesList.isN20()) {
            monitoringApproaches.put(MonitoringApproachType.N2O, N2OMonitoringApproach.builder().type(MonitoringApproachType.N2O).build());
        }
        if (etsMonitoringApproachesList.isCo2()) {
            monitoringApproaches.put(MonitoringApproachType.INHERENT_CO2, InherentCO2MonitoringApproach.builder().type(MonitoringApproachType.INHERENT_CO2).build());
            monitoringApproaches.put(MonitoringApproachType.TRANSFERRED_CO2, TransferredCO2MonitoringApproach.builder().type(MonitoringApproachType.TRANSFERRED_CO2).build());
        }
        return MonitoringApproaches.builder()
                .monitoringApproaches(monitoringApproaches)
                .build();
    }
}
