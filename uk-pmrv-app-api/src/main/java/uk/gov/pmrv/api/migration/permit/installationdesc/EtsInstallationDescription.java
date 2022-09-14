package uk.gov.pmrv.api.migration.permit.installationdesc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsInstallationDescription {

    private String etsAccountId;

    private String mainActivitiesDesc;
}
