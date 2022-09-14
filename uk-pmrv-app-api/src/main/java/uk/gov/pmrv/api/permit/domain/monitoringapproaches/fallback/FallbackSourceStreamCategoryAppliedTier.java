package uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FallbackSourceStreamCategoryAppliedTier {
    
    @Valid
    @NotNull
    private FallbackSourceStreamCategory sourceStreamCategory;
}
