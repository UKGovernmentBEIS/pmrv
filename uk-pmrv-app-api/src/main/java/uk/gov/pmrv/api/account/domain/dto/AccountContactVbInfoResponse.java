package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountContactVbInfoResponse {

    private List<AccountContactVbInfoDTO> contacts;

    private boolean editable;

    private Long totalItems;
}
