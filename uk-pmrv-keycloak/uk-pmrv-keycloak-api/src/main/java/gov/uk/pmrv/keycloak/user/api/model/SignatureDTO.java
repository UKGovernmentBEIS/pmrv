package gov.uk.pmrv.keycloak.user.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureDTO {

    private byte[] content;
    private String name;
    private Long size;
    private String type;
}
