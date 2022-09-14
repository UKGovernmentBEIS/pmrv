package uk.gov.pmrv.api.user.core.domain.model.keycloak;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeycloakSignature {

    private String name;
    private byte[] content;
    private Long size;
    private String type;
}
