package gov.uk.pmrv.keycloak.user.api.service;

import gov.uk.pmrv.keycloak.user.api.model.SignatureDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsRequestDTO;
import gov.uk.pmrv.keycloak.user.api.repository.UserDetailsRepository;
import gov.uk.pmrv.keycloak.user.api.transform.UserDetailsMapper;

public class UserDetailsService {

    private final UserSessionService userSessionService;
    private final UserDetailsRepository userDetailsRepository;
    private final UserDetailsMapper userDetailsMapper;

    public UserDetailsService(UserDetailsRepository userDetailsRepository,
            UserSessionService userSessionService,
            UserDetailsMapper userDetailsMapper) {
        this.userDetailsRepository = userDetailsRepository;
        this.userSessionService = userSessionService;
        this.userDetailsMapper = userDetailsMapper;
    }

    public UserDetailsDTO getUserDetails(String userId) {
        userSessionService.getAuthenticatedUser();
        UserDetailsDTO userDetails = userDetailsRepository.findUserDetails(userId)
                .map(userDetailsMapper::toUserDetailsDTO)
                .orElse(null);
        return userDetails;
    }
    
    public void saveUserDetails(UserDetailsRequestDTO userDetailsRequestDTO) {
        userSessionService.getAuthenticatedUser();
        userDetailsRepository.saveUserDetails(userDetailsRequestDTO);
    }
    
    public SignatureDTO getUserSignature(String signatureUuid) {
        userSessionService.getAuthenticatedUser();
        return userDetailsRepository.findUserSignatureBySignatureUuid(signatureUuid)
                .map(userDetailsMapper::toSignatureDTO).orElse(null);
    }

}
