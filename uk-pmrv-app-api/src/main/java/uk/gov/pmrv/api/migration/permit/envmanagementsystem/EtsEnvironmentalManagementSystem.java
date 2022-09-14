package uk.gov.pmrv.api.migration.permit.envmanagementsystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEnvironmentalManagementSystem {

    private String etsAccountId;

    private boolean exist;

    private boolean certified;

    private String certificationStandard;
}
