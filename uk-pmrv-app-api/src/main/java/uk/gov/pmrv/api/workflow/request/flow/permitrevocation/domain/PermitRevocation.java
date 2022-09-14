package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#activitiesStopped) == (#stoppedDate != null)}",
    message = "permitrevocation.incompatible.activities.stopped.with.stop.date")
@SpELExpression(expression = "{(#effectiveDate == null) || " +
    "T(java.time.LocalDate).now().plusDays(27).isBefore(T(java.time.LocalDate).parse(#effectiveDate))}",
    message = "permitrevocation.effective.date.too.soon")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#annualEmissionsReportRequired) == (#annualEmissionsReportDate != null)}",
    message = "permitrevocation.incompatible.report.required.with.report.date")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#surrenderRequired) == (#surrenderDate != null)}",
    message = "permitrevocation.incompatible.surrender.required.with.surrender.date")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#feeCharged) == (#feeDate != null)}",
    message = "permitrevocation.incompatible.fee.charged.with.fee.date")
@SpELExpression(expression = "{(#feeDate == null) || (#effectiveDate == null) || " +
    "T(java.time.LocalDate).parse(#feeDate).isAfter(T(java.time.LocalDate).parse(#effectiveDate))}",
    message = "permitrevocation.fee.date.not.after.effective.date")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#feeCharged) == (#feeDetails != null)}",
    message = "permitrevocation.incompatible.fee.charged.with.fee.details")
public class PermitRevocation {

    @NotBlank
    @Size(max = 10000)
    private String reason;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean activitiesStopped;

    @Past
    private LocalDate stoppedDate;

    @NotNull
    private LocalDate effectiveDate;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean annualEmissionsReportRequired;

    @FutureOrPresent
    private LocalDate annualEmissionsReportDate;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean surrenderRequired;

    @FutureOrPresent
    private LocalDate surrenderDate;
    
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean feeCharged;
    
    private LocalDate feeDate;

    @Size(max = 10000)
    private String feeDetails;
}
