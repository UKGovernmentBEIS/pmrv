package uk.gov.pmrv.api.permit.domain.monitoringapproaches;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitSection;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringApproaches implements PermitSection {

    @Valid
    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = new EnumMap<>(MonitoringApproachType.class);
    
    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return monitoringApproaches.values().stream()
                .map(PermitMonitoringApproachSection::getAttachmentIds)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
