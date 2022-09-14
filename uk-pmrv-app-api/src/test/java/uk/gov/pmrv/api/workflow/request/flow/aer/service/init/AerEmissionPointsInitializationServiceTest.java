package uk.gov.pmrv.api.workflow.request.flow.aer.service.init;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.common.SourceStreamCategoryType;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OEmissionType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;
import uk.gov.pmrv.api.reporting.domain.Aer;

class AerEmissionPointsInitializationServiceTest {
    
    private final AerEmissionPointsInitializationService service = new AerEmissionPointsInitializationService();

    @Test
    void initialize() {
        String epId1 = UUID.randomUUID().toString();
        String epId2 = UUID.randomUUID().toString();
        String epId3 = UUID.randomUUID().toString();
        String epId4 = UUID.randomUUID().toString();
        
        EmissionPoint ep1 = EmissionPoint.builder().id(epId1).reference("ref1").description("desc1").build();
        EmissionPoint ep2 = EmissionPoint.builder().id(epId2).reference("ref2").description("desc2").build();
        EmissionPoint ep3 = EmissionPoint.builder().id(epId3).reference("ref3").description("desc3").build();
        EmissionPoint ep4 = EmissionPoint.builder().id(epId4).reference("ref4").description("desc4").build();

        MeasMonitoringApproach measMonitoringApproach = MeasMonitoringApproach.builder()
            .type(MonitoringApproachType.MEASUREMENT)
            .sourceStreamCategoryAppliedTiers(List.of(MeasSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(MeasSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(epId1, epId4))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000))
                    .categoryType(SourceStreamCategoryType.MAJOR)
                    .build())
                .build()))
            .build();

        N2OMonitoringApproach n20MonitoringApproach = N2OMonitoringApproach.builder()
            .type(MonitoringApproachType.N2O)
            .sourceStreamCategoryAppliedTiers(List.of(N2OSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(N2OSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(epId2, epId4))
                    .emissionType(N2OEmissionType.ABATED)
                    .monitoringApproachType(N2OMonitoringApproachType.CALCULATION)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(27500))
                    .categoryType(SourceStreamCategoryType.MAJOR)
                    .build())
                .build()))
            .build();

        PFCMonitoringApproach pfcMonitoringApproach = PFCMonitoringApproach.builder()
            .type(MonitoringApproachType.PFC)
            .sourceStreamCategoryAppliedTiers(List.of(PFCSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(PFCSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(epId3))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(25000))
                    .categoryType(SourceStreamCategoryType.MAJOR)
                    .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                    .build())
                .build()))
            .build();

        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            new EnumMap<>(MonitoringApproachType.class);
        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT, measMonitoringApproach);
        monitoringApproaches.put(MonitoringApproachType.N2O, n20MonitoringApproach);
        monitoringApproaches.put(MonitoringApproachType.PFC, pfcMonitoringApproach);

        Aer aer = Aer.builder().monitoringApproachTypes(monitoringApproaches.keySet()).build();
        
        Permit permit = Permit.builder()
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(ep1, ep2, ep3, ep4)).build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build())
            .build();

        service.initialize(aer, permit);

        assertNotNull(aer.getEmissionPoints());
        assertThat(aer.getEmissionPoints().getEmissionPoints()).containsExactlyInAnyOrder(ep1, ep2, ep4);
        assertThat(aer.getMonitoringApproachTypes()).containsExactlyInAnyOrderElementsOf(monitoringApproaches.keySet());
    }

    @Test
    void initialize_with_null() {
        String epId1 = UUID.randomUUID().toString();
        String epId2 = UUID.randomUUID().toString();
        EmissionPoint ep1 = EmissionPoint.builder().id(epId1).reference("ref1").description("desc1").build();
        EmissionPoint ep2 = EmissionPoint.builder().id(epId2).reference("ref2").description("desc2").build();

        TransferredCO2MonitoringApproach transferredCO2MonitoringApproach = TransferredCO2MonitoringApproach.builder()
            .type(MonitoringApproachType.TRANSFERRED_CO2)
            .build();

        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            new EnumMap<>(MonitoringApproachType.class);
        monitoringApproaches.put(MonitoringApproachType.TRANSFERRED_CO2, transferredCO2MonitoringApproach);

        Aer aer = Aer.builder().monitoringApproachTypes(monitoringApproaches.keySet()).build();

        Permit permit = Permit.builder()
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(ep1, ep2)).build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build())
            .build();

        service.initialize(aer, permit);

        assertNull(aer.getEmissionPoints());
        assertThat(aer.getMonitoringApproachTypes()).containsExactlyInAnyOrderElementsOf(monitoringApproaches.keySet());
    }
}