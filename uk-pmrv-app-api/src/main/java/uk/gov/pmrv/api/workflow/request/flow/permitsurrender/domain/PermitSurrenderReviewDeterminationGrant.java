package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#noticeDate == null) || T(java.time.LocalDate).now().plusDays(27).isBefore(T(java.time.LocalDate).parse(#noticeDate))}",
    message = "permitsurrender.reviewdetermination.grant.noticedate.lessthan")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#reportRequired) == (#reportDate != null)}", 
    message = "permitsurrender.reviewdetermination.grant.report")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#allowancesSurrenderRequired) == (#allowancesSurrenderDate != null)}", 
    message = "permitsurrender.reviewdetermination.grant.allowancessurrender")
public class PermitSurrenderReviewDeterminationGrant extends PermitSurrenderReviewDetermination {
    
    @PastOrPresent
    @NotNull
    private LocalDate stopDate;
    
    @NotNull
    private LocalDate noticeDate;
    
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean reportRequired;
    
    @FutureOrPresent
    private LocalDate reportDate;
    
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean allowancesSurrenderRequired;
    
    @FutureOrPresent
    private LocalDate allowancesSurrenderDate;
}
