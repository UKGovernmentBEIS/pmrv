package uk.gov.pmrv.api.migration.regulatoruser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsRegulatorUser {
    
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String phoneNumber;
    private String roleName;
    private String compAuth;
    
}
