package uk.gov.pmrv.api.migration.installationaccount.contacts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaSiteContact {

    private String emitterId;
    private String caContactId;
    private String emitterName;
    private String emitterDisplayId;
    private String caContactEmail;
}
