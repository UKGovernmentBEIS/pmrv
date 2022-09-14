package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

/**
 * The RequestActionRegistrationDTO for creating a new request.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateActionProcessDTO {

    @NotNull(message = "{requestActionProcess.requestCreateActionType.notEmpty}")
    private RequestCreateActionType requestCreateActionType;

    @NotNull(message = "{requestActionProcess.requestCreateActionPayload.notEmpty}")
    @Valid
    private RequestCreateActionPayload requestCreateActionPayload;

}
