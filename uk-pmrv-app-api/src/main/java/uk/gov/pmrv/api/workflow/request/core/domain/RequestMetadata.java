package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestMetadata;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AerRequestMetadata.class, name = "AER"),
        @JsonSubTypes.Type(value = PermitIssuanceRequestMetadata.class, name = "PERMIT_ISSUANCE"),
        @JsonSubTypes.Type(value = PermitSurrenderRequestMetadata.class, name = "PERMIT_SURRENDER"),
        @JsonSubTypes.Type(value = PermitVariationRequestMetadata.class, name = "PERMIT_VARIATION"),
        @JsonSubTypes.Type(value = PermitNotificationRequestMetadata.class, name = "PERMIT_NOTIFICATION"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestMetadata {

    private RequestMetadataType type;
}
