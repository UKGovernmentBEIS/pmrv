package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.Determinateable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PermitIssuanceGrantDetermination.class, name = "GRANTED"),
    @JsonSubTypes.Type(value = PermitIssuanceRejectDetermination.class, name = "REJECTED"),
    @JsonSubTypes.Type(value = PermitIssuanceDeemedWithdrawnDetermination.class, name = "DEEMED_WITHDRAWN"),
})
public interface PermitIssuanceDeterminateable extends Determinateable {

}
