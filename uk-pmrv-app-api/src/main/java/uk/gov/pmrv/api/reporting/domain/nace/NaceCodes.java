package uk.gov.pmrv.api.reporting.domain.nace;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaceCodes {
    @NotEmpty
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<NaceCode> codes = new HashSet<>();
}
