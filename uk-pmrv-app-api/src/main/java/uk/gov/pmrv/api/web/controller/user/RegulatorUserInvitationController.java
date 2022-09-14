package uk.gov.pmrv.api.web.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.util.FileDtoMapper;

@RestController
@RequestMapping(path = "/v1.0/regulator-users/invite")
@Api(tags = "Regulator Users")
@RequiredArgsConstructor
public class RegulatorUserInvitationController {

    private final RegulatorUserInvitationService regulatorUserInvitationService;
    private final FileDtoMapper fileDtoMapper = Mappers.getMapper(FileDtoMapper.class);

    @PostMapping
    @ApiOperation(value = "Invite new regulator user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> inviteRegulatorUserToCA(
        PmrvUser currentUser,
        @RequestPart @Valid @ApiParam(value = "The regulator to invite", required = true) RegulatorInvitedUserDTO regulatorInvitedUser,
        @RequestPart(value = "signature", required = false) @NotNull @Valid @ApiParam(value = "The signature file", required = false) MultipartFile signature) throws IOException {
        FileDTO signatureDTO = fileDtoMapper.toFileDTO(signature);
        regulatorUserInvitationService.inviteRegulatorUser(regulatorInvitedUser, signatureDTO, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
