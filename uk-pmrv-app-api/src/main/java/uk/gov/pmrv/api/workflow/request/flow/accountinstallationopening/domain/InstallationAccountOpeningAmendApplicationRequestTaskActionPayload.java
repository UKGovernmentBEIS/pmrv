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

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InstallationAccountOpeningAmendApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @JsonUnwrapped
    @Valid
    @NotNull
    private AccountPayload accountPayload;
}
