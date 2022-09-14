package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningSubmitApplicationCreateActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = RequestCreateActionEmptyPayload.class, name = "EMPTY_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestCreateActionPayload {

    private RequestCreateActionPayloadType payloadType;
}
