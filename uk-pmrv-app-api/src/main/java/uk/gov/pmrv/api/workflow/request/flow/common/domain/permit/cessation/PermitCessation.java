package uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitCessation {

    @NotNull
    private PermitCessationDeterminationOutcome determinationOutcome;

    @PastOrPresent
    private LocalDate allowancesSurrenderDate;

    @Min(value = 0)
    @Max(value = 99999999)
    private Integer numberOfSurrenderAllowances;

    @NotNull
    @DecimalMin(value = "0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal annualReportableEmissions;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean subsistenceFeeRefunded;

    @NotNull
    private PermitCessationOfficialNoticeType noticeType;

    @NotBlank
    @Size(max = 10000)
    private String notes;
}
