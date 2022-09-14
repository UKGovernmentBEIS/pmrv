package gov.uk.pmrv.keycloak.user.api.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import gov.uk.pmrv.keycloak.user.api.model.SignatureDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsDTO;
import gov.uk.pmrv.keycloak.user.api.model.jpa.Signature;
import gov.uk.pmrv.keycloak.user.api.model.jpa.UserDetails;

public class UserDetailsMapperTest {

    @Test
    void toUserDetailsDTO() {
        UserDetails userDetails = new UserDetails();
        userDetails.setId("userId1");
        
        String signatureUuid = UUID.randomUUID().toString();
        userDetails.setSignature(new Signature(signatureUuid, "content".getBytes(), "fileName", 1L, "type"));
        
        UserDetailsMapper mapper = new UserDetailsMapper();
        UserDetailsDTO result = mapper.toUserDetailsDTO(userDetails);
        
        assertThat(result.getId()).isEqualTo("userId1");
        assertThat(result.getSignature().getUuid()).isEqualTo(signatureUuid);
        assertThat(result.getSignature().getName()).isEqualTo("fileName");
    }
    
    @Test
    void toSignatureDTO() {
        String signatureUuid = UUID.randomUUID().toString();
        Signature signature = new Signature(signatureUuid, "content".getBytes(), "fileName", 1L, "type");
        
        UserDetailsMapper mapper = new UserDetailsMapper();
        SignatureDTO result = mapper.toSignatureDTO(signature);
        
        assertThat(result.getContent()).isEqualTo("content".getBytes());
        assertThat(result.getName()).isEqualTo("fileName");
        assertThat(result.getSize()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo("type");
    }
}
