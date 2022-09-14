package uk.gov.pmrv.api.account.domain.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountUpdateRegistryIdDTO {

    @Min(1000000)
    @Max(9999999)
    private Integer registryId;
}
