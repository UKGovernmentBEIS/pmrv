package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;

@UtilityClass
public class PermitReviewGroupMonitoringApproachMapper {

    private static final ImmutableMap<MonitoringApproachType, PermitReviewGroup> MAP =
        ImmutableMap.<MonitoringApproachType, PermitReviewGroup>builder()
            .put(MonitoringApproachType.CALCULATION, PermitReviewGroup.CALCULATION)
            .put(MonitoringApproachType.MEASUREMENT, PermitReviewGroup.MEASUREMENT)
            .put(MonitoringApproachType.FALLBACK, PermitReviewGroup.FALLBACK)
            .put(MonitoringApproachType.N2O, PermitReviewGroup.N2O)
            .put(MonitoringApproachType.PFC, PermitReviewGroup.PFC)
            .put(MonitoringApproachType.INHERENT_CO2, PermitReviewGroup.INHERENT_CO2)
            .put(MonitoringApproachType.TRANSFERRED_CO2, PermitReviewGroup.TRANSFERRED_CO2)
            .build();

    public static PermitReviewGroup getPermitReviewGroupFromMonitoringApproach(final MonitoringApproachType type) {
        return MAP.get(type);
    }
}