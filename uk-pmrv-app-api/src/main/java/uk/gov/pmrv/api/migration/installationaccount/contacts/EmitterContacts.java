package uk.gov.pmrv.api.migration.installationaccount.contacts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitterContacts {

    private String emitterId;
    private String emitterName;
    private String emitterDisplayId;
    private String primaryContactId;
    private String primaryContactEmail;
    private String secondaryContactId;
    private String secondaryContactEmail;
    private String serviceContactId;
    private String serviceContactEmail;
    private String financeContactId;
    private String financeContactEmail;
}
