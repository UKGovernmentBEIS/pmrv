package gov.uk.pmrv.keycloak.user.api.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private String id;

    private String firstName;

    private String lastName;
    
    private String email;

    private boolean enabled;

    private Map<String, String> attributes;
}
