package uk.gov.pmrv.api.workflow.request.flow.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AerRequestMetadata extends RequestMetadata {

    @NotNull
    @PastOrPresent
    private Year year;

    @NotNull
    @Positive
    private BigDecimal emissions;
}
