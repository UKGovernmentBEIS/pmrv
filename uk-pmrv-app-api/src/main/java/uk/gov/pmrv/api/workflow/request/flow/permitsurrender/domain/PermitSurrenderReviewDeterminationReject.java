package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitSurrenderReviewDeterminationReject extends PermitSurrenderReviewDetermination {

    @NotBlank
    @Size(max = 10000)
    private String officialRefusalLetter;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean shouldFeeBeRefundedToOperator;
}
