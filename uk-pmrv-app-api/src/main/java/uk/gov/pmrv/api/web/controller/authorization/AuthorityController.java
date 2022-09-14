package uk.gov.pmrv.api.web.controller.authorization;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.authorization.core.domain.dto.UserStatusDTO;
import uk.gov.pmrv.api.authorization.core.service.UserStatusService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/authorities")
@Api(tags = "Authorities")
@RequiredArgsConstructor
public class AuthorityController {

    private final UserStatusService userStatusService;

    @GetMapping(path = "/current-user-status")
    @ApiOperation(value = "Retrieves the status of the logged in user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = UserStatusDTO.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<UserStatusDTO> getCurrentUserStatus(PmrvUser pmrvUser) {
        return new ResponseEntity<>(
            UserStatusDTO.builder()
                .userId(pmrvUser.getUserId())
                .roleType(pmrvUser.getRoleType())
                .loginStatus(userStatusService.getLoginStatus(pmrvUser.getUserId()))
                .build(), HttpStatus.OK);
    }
}
