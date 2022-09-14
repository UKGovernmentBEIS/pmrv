package uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeteringUncertainty;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.SourceStreamCategoryBase;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FallbackSourceStreamCategory extends SourceStreamCategoryBase {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> measurementDevicesOrMethods = new LinkedHashSet<>();
    
    private MeteringUncertainty uncertainty;
}
