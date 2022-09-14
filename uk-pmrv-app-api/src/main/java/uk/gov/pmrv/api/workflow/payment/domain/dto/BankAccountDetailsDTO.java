package uk.gov.pmrv.api.workflow.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountDetailsDTO {

    private String sortCode;
    private String accountNumber;
    private String accountName;
    private String iban;
    private String swiftCode;
}
