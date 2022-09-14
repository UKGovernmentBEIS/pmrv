package uk.gov.pmrv.api.workflow.request.flow.common.domain.permit;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.validation.constraints.Digits;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class GrantDetermination extends Determination {
    
    @FutureOrPresent
    private LocalDate activationDate;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private SortedMap<String, @Digits(integer = 8, fraction = 1) @Positive BigDecimal> annualEmissionsTargets = new TreeMap<>();
}
