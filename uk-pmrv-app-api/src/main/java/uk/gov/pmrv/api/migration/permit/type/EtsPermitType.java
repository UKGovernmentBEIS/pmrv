package uk.gov.pmrv.api.migration.permit.type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsPermitType {

    private String etsAccountId;
    private String type;
}
