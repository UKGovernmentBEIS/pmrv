package uk.gov.pmrv.api.migration.permit.regulatedactivities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatedActivity {
    
    private String etsAccountId;

    private String type;
    private String quantity;
    private String quantityUnit;
    private String greenHousesCategory;
}
