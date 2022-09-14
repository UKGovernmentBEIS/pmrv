package uk.gov.pmrv.api.migration.permit.confidentialitystatement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsConfidentialityStatement {

    private String etsAccountId;
    private boolean exist;
    private String section;
    private String justification;
}
