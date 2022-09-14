package uk.gov.pmrv.api.permit.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitValidationResult {

    private boolean valid;

    @Builder.Default
    private List<PermitViolation> permitViolations = new ArrayList<>();

    public static PermitValidationResult validPermit() {
        return PermitValidationResult.builder().valid(true).build();
    }

    public static PermitValidationResult invalidPermit(List<PermitViolation> permitViolations) {
        return PermitValidationResult.builder().valid(false).permitViolations(permitViolations).build();
    }
}
