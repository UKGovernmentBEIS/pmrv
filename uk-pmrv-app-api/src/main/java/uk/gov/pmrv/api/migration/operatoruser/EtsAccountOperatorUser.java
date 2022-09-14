package uk.gov.pmrv.api.migration.operatoruser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsAccountOperatorUser {
    
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
    private String compAuth;
    private String emitterName;
    private String emitterId;
}
