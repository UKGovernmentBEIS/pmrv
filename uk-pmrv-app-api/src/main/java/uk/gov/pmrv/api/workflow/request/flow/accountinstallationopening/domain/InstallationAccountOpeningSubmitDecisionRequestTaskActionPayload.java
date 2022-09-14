package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

/**
 * The DTO for the Submit Decision Action of the Installation Account Opening request type
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    @JsonUnwrapped
    @Valid
    @NotNull
    private AccountOpeningDecisionPayload accountOpeningDecisionPayload;
}
