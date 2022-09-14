package gov.uk.pmrv.keycloak.user.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureInfoDTO {

    private String uuid;
    private String name;
    
}
