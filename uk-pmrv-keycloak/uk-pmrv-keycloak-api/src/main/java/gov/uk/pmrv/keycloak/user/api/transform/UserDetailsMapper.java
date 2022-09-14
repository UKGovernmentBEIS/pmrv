package gov.uk.pmrv.keycloak.user.api.transform;

import gov.uk.pmrv.keycloak.user.api.model.SignatureDTO;
import gov.uk.pmrv.keycloak.user.api.model.SignatureInfoDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsDTO;
import gov.uk.pmrv.keycloak.user.api.model.jpa.Signature;
import gov.uk.pmrv.keycloak.user.api.model.jpa.UserDetails;

public class UserDetailsMapper {

    public UserDetailsDTO toUserDetailsDTO(UserDetails userDetails) {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setId(userDetails.getId());
        if(userDetails.getSignature() != null &&
                userDetails.getSignature().getSignatureUuid() != null) {
            userDetailsDTO.setSignature(toSignatureInfoDTO(userDetails.getSignature())); 
        }
        return userDetailsDTO;
    }

    public SignatureDTO toSignatureDTO(Signature signature) {
        if(signature == null) {
            return null;
        }
        
        return new SignatureDTO(signature.getSignatureContent(), signature.getSignatureName(),
                signature.getSignatureSize(), signature.getSignatureType());
    }
    
    private SignatureInfoDTO toSignatureInfoDTO(Signature signature) {
        if(signature == null) {
            return null;
        }
        return new SignatureInfoDTO(signature.getSignatureUuid(), signature.getSignatureName());
    }
}
