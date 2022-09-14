package uk.gov.pmrv.api.permit.domain.sitediagram;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitSection;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SiteDiagrams implements PermitSection {
    
    @Builder.Default
    private Set<UUID> siteDiagrams = new HashSet<>();
}
