package gov.uk.pmrv.keycloak.user.api.controller;

import gov.uk.pmrv.keycloak.user.api.model.SignatureDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsRequestDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserInfo;
import gov.uk.pmrv.keycloak.user.api.model.UserOtpValidationDTO;
import gov.uk.pmrv.keycloak.user.api.service.UserDetailsService;
import gov.uk.pmrv.keycloak.user.api.service.UserEntityService;
import gov.uk.pmrv.keycloak.user.api.service.UserOtpService;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class UserController {

    private final UserEntityService userEntityService;
    private final UserDetailsService userDetailsService;
    private final UserOtpService userOtpService;

    public UserController(UserEntityService userEntityService, UserDetailsService userDetailsService, UserOtpService userOtpService) {
        this.userEntityService = userEntityService;
        this.userDetailsService = userDetailsService;
        this.userOtpService = userOtpService;
    }

    @POST
    @Path("")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserInfo> getUsers(List<String> userIds,
                                   @QueryParam("includeAttributes") boolean includeAttributes) {
        return userEntityService.getUsersInfo(userIds, includeAttributes);
    }
    
    @GET
    @Path("/user/details")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public UserDetailsDTO getUserDetails(@QueryParam("userId") String userId) {
        return userDetailsService.getUserDetails(userId);
    }
    
    @POST
    @Path("/user/details")
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void saveUserDetails(UserDetailsRequestDTO userDetailsRequestDTO) {
        userDetailsService.saveUserDetails(userDetailsRequestDTO);
    }
    
    @GET
    @Path("/user/signature")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public SignatureDTO getUserSignature(@QueryParam("signatureUuid") String signatureUuid) {
        return userDetailsService.getUserSignature(signatureUuid);
    }

    @POST
    @Path("/otp/validation")
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void validateUserOtp(UserOtpValidationDTO userOtpValidationDTO) {
        userOtpService.validateUserOtp(userOtpValidationDTO);
    }

}
