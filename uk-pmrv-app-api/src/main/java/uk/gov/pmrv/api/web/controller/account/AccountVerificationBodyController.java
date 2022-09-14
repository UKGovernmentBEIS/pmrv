package uk.gov.pmrv.api.web.controller.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.AppointVerificationBodyDTO;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyAppointService;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyService;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1.0/accounts")
@RequiredArgsConstructor
@Api(tags = "Account verification body")
public class AccountVerificationBodyController {
    
    private final AccountVerificationBodyService accountVerificationBodyService;
    private final AccountVerificationBodyAppointService accountVerificationBodyAppointService;

    @GetMapping("/{id}/verification-body")
    @ApiOperation(value = "Get the verification body of the account (if exists)", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerificationBodyNameInfoDTO.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<VerificationBodyNameInfoDTO> getVerificationBodyOfAccount(
            @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId) {
        return new ResponseEntity<>(
                accountVerificationBodyService.getVerificationBodyNameInfoByAccount(accountId).orElse(null),
                HttpStatus.OK);
    }
    
    @GetMapping(path = "/{id}/active-verification-bodies")
    @ApiOperation(value = "Get all active verification bodies", 
        tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerificationBodyNameInfoDTO.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<List<VerificationBodyNameInfoDTO>> getActiveVerificationBodies(
            @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId) {
        return new ResponseEntity<>(
                accountVerificationBodyService.getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(accountId),
                HttpStatus.OK);
    }
    
    @PostMapping(path = "/{id}/appoint-verification-body")
    @ApiOperation(value = "Appoint verification body to account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.APPOINT_VERIFICATION_BODY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> appointVerificationBodyToAccount(
            @PathVariable("id") @ApiParam(value = "The account id", required = true) 
                Long accountId,
            @RequestBody @Valid @ApiParam(value = "The verification body id to appoint to account", required = true)
                AppointVerificationBodyDTO appointVerificationBodyDTO) {
        accountVerificationBodyAppointService.appointVerificationBodyToAccount(appointVerificationBodyDTO.getVerificationBodyId(), accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{id}/appoint-verification-body")
    @ApiOperation(value = "Reappoint verification body to account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
            @ApiResponse(code = 400, message = SwaggerApiInfo.REAPPOINT_VERIFICATION_BODY_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> replaceVerificationBodyToAccount(
            @PathVariable("id") @ApiParam(value = "The account id", required = true)
                    Long accountId,
            @RequestBody @Valid @ApiParam(value = "The verification body id to appoint to account", required = true)
                    AppointVerificationBodyDTO appointVerificationBodyDTO) {
        accountVerificationBodyAppointService.replaceVerificationBodyToAccount(appointVerificationBodyDTO.getVerificationBodyId(), accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
