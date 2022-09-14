package uk.gov.pmrv.api.workflow.request.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTaskActionValidationResult {

    private boolean valid;

    private ErrorMessage errorMessage;

    public static RequestTaskActionValidationResult validResult() {
        return RequestTaskActionValidationResult.builder().valid(true).build();
    }

    public static RequestTaskActionValidationResult invalidResult(ErrorMessage errorMessage) {
        return RequestTaskActionValidationResult.builder().valid(false).errorMessage(errorMessage).build();
    }

    @Getter
    public enum ErrorMessage {
        RFI_RDE_ALREADY_EXISTS,
        PAYMENT_IN_PROGRESS
    }
}
