package uk.gov.pmrv.api.mireport.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountUserContact {

    private AccountType accountType;

    private Long accountId;

    private String accountName;

    private AccountStatus accountStatus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitType;

    private String legalEntityName;

    @JsonProperty(value = "isPrimaryContact")
    private boolean isPrimaryContact;

    @JsonProperty(value = "isSecondaryContact")
    private boolean isSecondaryContact;

    @JsonProperty(value = "isFinancialContact")
    private boolean isFinancialContact;

    @JsonProperty(value = "isServiceContact")
    private boolean isServiceContact;

    private AuthorityStatus authorityStatus;

    @JsonInclude(Include.NON_NULL)
    private String name;

    @JsonInclude(Include.NON_NULL)
    private String telephone;

    @JsonInclude(Include.NON_NULL)
    private String lastLogon;

    @JsonInclude(Include.NON_NULL)
    private String email;

    private String role;
}
