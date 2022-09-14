package uk.gov.pmrv.api.migration.installationaccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emitter {

    private String id;
    private String name;
    private String emitterDisplayId;
    private String siteName;
    private String competentAuthority;
    private String status;
    private String legalEntityId;
    private String legalEntityName;
    private String locationType;
    private String gridReference;
    private LocalDateTime acceptedDate;
    private String commencementDate;
    private String locationLine1;
    private String locationLine2;
    private String city;
    private String country;
    private String postCode;
    private String longitude;
    private String latitude;
    private String vbId;
    private String vbName;
    private Long sopId;
    private String scheme;
}
