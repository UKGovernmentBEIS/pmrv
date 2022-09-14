package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountContactVbInfoDTO {

    private Long accountId;

    private String accountName;

    private String type;

    private String userId;
}
