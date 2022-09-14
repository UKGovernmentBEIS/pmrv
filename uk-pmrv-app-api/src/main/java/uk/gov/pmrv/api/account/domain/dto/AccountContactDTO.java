package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountContactDTO {

    @NotNull(message = "{accountContact.accountId.notEmpty}")
    private Long accountId;

    private String userId;
}
