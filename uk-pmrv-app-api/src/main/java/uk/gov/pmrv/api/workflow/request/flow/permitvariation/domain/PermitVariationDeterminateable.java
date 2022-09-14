package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.Determinateable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PermitVariationGrantDetermination.class, name = "GRANTED"),
    @JsonSubTypes.Type(value = PermitVariationRejectDetermination.class, name = "REJECTED"),
    @JsonSubTypes.Type(value = PermitVariationDeemedWithdrawnDetermination.class, name = "DEEMED_WITHDRAWN"),
})
public interface PermitVariationDeterminateable extends Determinateable {

}
