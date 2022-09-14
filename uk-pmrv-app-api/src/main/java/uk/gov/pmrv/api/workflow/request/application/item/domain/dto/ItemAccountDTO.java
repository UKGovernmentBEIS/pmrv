package uk.gov.pmrv.api.workflow.request.application.item.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemAccountDTO {

    private Long accountId;

    private String accountName;

    private CompetentAuthority competentAuthority;

    private String leName;
}
