package gov.uk.pmrv.keycloak.user.api.service;

import gov.uk.pmrv.keycloak.user.api.model.SignatureDTO;
import gov.uk.pmrv.keycloak.user.api.model.SignatureInfoDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsRequestDTO;
import gov.uk.pmrv.keycloak.user.api.model.jpa.Signature;
import gov.uk.pmrv.keycloak.user.api.model.jpa.UserDetails;
import gov.uk.pmrv.keycloak.user.api.repository.UserDetailsRepository;
import gov.uk.pmrv.keycloak.user.api.transform.UserDetailsMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @InjectMocks
    private UserDetailsService service;

    @Mock
    private UserDetailsRepository userDetailsRepository;
    
    @Mock
    private UserSessionService userSessionService;

    @Mock
    private UserDetailsMapper userDetailsMapper;
    
    @Test
    void getUserDetails() {
        String userId = "userId";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        
        String signatureUuid = UUID.randomUUID().toString();
        Signature signature = new Signature(signatureUuid, "content".getBytes(), "fileName", 1L, "type");
        
        UserDetails userDetails = new UserDetails();
        userDetails.setId(userId);
        userDetails.setUser(userEntity);
        userDetails.setSignature(signature);
        
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(userId, new SignatureInfoDTO(signatureUuid, "fileName"));
        
        when(userDetailsRepository.findUserDetails(userId)).thenReturn(Optional.of(userDetails));
        when(userDetailsMapper.toUserDetailsDTO(userDetails)).thenReturn(userDetailsDTO);
        
        //invoke
        UserDetailsDTO result = service.getUserDetails(userId);
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getSignature()).isEqualTo(new SignatureInfoDTO(signatureUuid, "fileName"));
        verify(userDetailsRepository, times(1)).findUserDetails(userId);
        verify(userDetailsMapper, times(1)).toUserDetailsDTO(userDetails);
    }
    
    @Test
    void saveUserDetails() {
        String userId = "userId";
        SignatureDTO newSignatureDTO = new SignatureDTO("content".getBytes(), "fileName", 1L, "type2");
        UserDetailsRequestDTO userDetailsRequestDTO = new UserDetailsRequestDTO(userId, newSignatureDTO);
        
        service.saveUserDetails(userDetailsRequestDTO);
        verify(userDetailsRepository, times(1)).saveUserDetails(userDetailsRequestDTO);
    }
    
    @Test
    void getUserSignature() {
        String signatureUuid = UUID.randomUUID().toString();
        Signature signature = new Signature(signatureUuid, "content".getBytes(), "signaturename", 1L, "type");
        SignatureDTO signatureDTO = new SignatureDTO(signature.getSignatureContent(), signature.getSignatureName(), signature.getSignatureSize(), signature.getSignatureType());
        
        when(userDetailsRepository.findUserSignatureBySignatureUuid(signatureUuid)).thenReturn(Optional.of(signature));
        when(userDetailsMapper.toSignatureDTO(signature)).thenReturn(signatureDTO);
        
        
        SignatureDTO result = service.getUserSignature(signatureUuid);
        assertThat(result).isEqualTo(signatureDTO);
        verify(userDetailsRepository, times(1)).findUserSignatureBySignatureUuid(signatureUuid);
        verify(userDetailsMapper, times(1)).toSignatureDTO(signature);
    }

}
