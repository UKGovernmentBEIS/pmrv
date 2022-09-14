package uk.gov.pmrv.api.account.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountContactInfoResponse {

    private List<AccountContactInfoDTO> contacts;

    private boolean editable;

    private Long totalItems;
}
