package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PermitSurrenderReviewDeterminationGrant.class, name = "GRANTED"),
    @JsonSubTypes.Type(value = PermitSurrenderReviewDeterminationReject.class, name = "REJECTED"),
    @JsonSubTypes.Type(value = PermitSurrenderReviewDeterminationDeemWithdraw.class, name = "DEEMED_WITHDRAWN")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PermitSurrenderReviewDetermination {

    private PermitSurrenderReviewDeterminationType type;

    @NotNull
    @Size(max = 10000)
    private String reason;
}
