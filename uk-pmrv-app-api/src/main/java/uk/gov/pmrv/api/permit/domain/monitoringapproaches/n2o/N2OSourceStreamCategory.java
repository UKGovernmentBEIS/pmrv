package uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.SourceStreamCategoryBase;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class N2OSourceStreamCategory extends SourceStreamCategoryBase {
    
    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionPoints = new LinkedHashSet<>();
    
    @NotNull
    private N2OEmissionType emissionType;

    @NotNull
    private N2OMonitoringApproachType monitoringApproachType;
}
