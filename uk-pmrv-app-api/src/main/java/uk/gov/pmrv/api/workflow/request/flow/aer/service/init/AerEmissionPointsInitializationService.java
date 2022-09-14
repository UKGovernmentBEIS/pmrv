package uk.gov.pmrv.api.workflow.request.flow.aer.service.init;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproach;
import uk.gov.pmrv.api.reporting.domain.Aer;

@Service
public class AerEmissionPointsInitializationService implements AerSectionInitializationService{

    public void initialize(Aer aer, Permit permit) {
        Map<MonitoringApproachType, PermitMonitoringApproachSection> permitMonitoringApproaches =
            permit.getMonitoringApproaches().getMonitoringApproaches();
        Set<MonitoringApproachType> permitMonitoringApproachTypes = new HashSet<>(permitMonitoringApproaches.keySet());

        permitMonitoringApproachTypes.retainAll(Set.of(MonitoringApproachType.MEASUREMENT, MonitoringApproachType.N2O));
        if(permitMonitoringApproachTypes.isEmpty()) {
            return;
        }

        List<EmissionPoint> permitEmissionPoints = permit.getEmissionPoints().getEmissionPoints();
        Set<String> referencedEmissionPointIds = getEmissionPointsReferencedInMeasAndN2OApproaches(permitMonitoringApproaches);

        List<EmissionPoint> aerEmissionPoints = permitEmissionPoints.stream()
            .filter(emissionPoint -> referencedEmissionPointIds.stream().anyMatch(id -> id.equals(emissionPoint.getId())))
            .collect(Collectors.toList());

        aer.setEmissionPoints(EmissionPoints.builder().emissionPoints(aerEmissionPoints).build());
}

    private Set<String> getEmissionPointsReferencedInMeasAndN2OApproaches(Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches) {
        Set<String> emissionPointIds = new HashSet<>();

        MeasMonitoringApproach measMonitoringApproach =
            (MeasMonitoringApproach) monitoringApproaches.get(MonitoringApproachType.MEASUREMENT);
        if(measMonitoringApproach != null) {
            emissionPointIds.addAll(measMonitoringApproach.getEmissionPoints());
        }

        N2OMonitoringApproach n2oMonitoringApproach =
            (N2OMonitoringApproach) monitoringApproaches.get(MonitoringApproachType.N2O);
        if(n2oMonitoringApproach != null) {
            emissionPointIds.addAll(n2oMonitoringApproach.getEmissionPoints());
        }

        return emissionPointIds;
    }
}
