package uk.gov.pmrv.api.web.controller.user;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_CA;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.UPDATE_REGULATOR_USER_BAD_REQUEST;

import java.io.IOException;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.core.service.UserSignatureService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserUpdateDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserManagementService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.RegulatorUserAuthorityUpdateOrchestrator;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;
import uk.gov.pmrv.api.web.util.FileDtoMapper;

@RestController
@RequestMapping(path = "/v1.0/regulator-users")
@Api(tags = "Regulator Users")
@RequiredArgsConstructor
public class RegulatorUserManagementController {

    private final RegulatorUserAuthorityUpdateOrchestrator regulatorUserAuthorityUpdateOrchestrator;
    private final RegulatorUserManagementService regulatorUserManagementService;
    private final UserSignatureService userSignatureService;
    private final FileDtoMapper fileDtoMapper = Mappers.getMapper(FileDtoMapper.class);

    @GetMapping(path = "/{userId}")
    @ApiOperation(value = "Retrieves the user of type REGULATOR that corresponds to the provided user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RegulatorUserDTO.class),
        @ApiResponse(code = 400, message = AUTHORITY_USER_NOT_RELATED_TO_CA, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<RegulatorUserDTO> getRegulatorUserByCaAndId(
        PmrvUser pmrvUser,
        @PathVariable("userId") @ApiParam(value = "The regulator user id") String userId) {
        return new ResponseEntity<>(regulatorUserManagementService.getRegulatorUserByUserId(pmrvUser, userId),
            HttpStatus.OK);
    }

    @PostMapping(path = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Updates the user of type REGULATOR that corresponds to the provided user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RegulatorUserUpdateDTO.class),
        @ApiResponse(code = 400, message = UPDATE_REGULATOR_USER_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<RegulatorUserUpdateDTO> updateRegulatorUserByCaAndId(
        PmrvUser currentUser,
        @PathVariable("userId") @ApiParam(value = "The regulator user id to update") String userId,
        @RequestPart @Valid @ApiParam(value = "The regulator user to update", required = true) RegulatorUserUpdateDTO regulatorUserUpdateDTO,
        @RequestPart(name = "signature", required = false) @Valid @ApiParam(value = "The signature file", required = false) MultipartFile signature
    ) throws IOException {
        FileDTO signatureDTO = fileDtoMapper.toFileDTO(signature);
        regulatorUserAuthorityUpdateOrchestrator.updateRegulatorUserByUserId(currentUser, userId, regulatorUserUpdateDTO, signatureDTO);
        return new ResponseEntity<>(regulatorUserUpdateDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Updates the current regulator user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RegulatorUserUpdateDTO.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = RoleType.REGULATOR)
    public ResponseEntity<RegulatorUserUpdateDTO> updateCurrentRegulatorUser(
        PmrvUser currentUser,
        @RequestPart @Valid @ApiParam(value = "The regulator user to update", required = true) RegulatorUserUpdateDTO regulatorUserUpdateDTO,
        @RequestPart(name = "signature", required = false) @Valid @ApiParam(value = "The signature file", required = false) MultipartFile signature
    ) throws IOException {
        FileDTO signatureDTO = fileDtoMapper.toFileDTO(signature);
        regulatorUserAuthorityUpdateOrchestrator
            .updateRegulatorUserByUserId(currentUser, currentUser.getUserId(), regulatorUserUpdateDTO, signatureDTO);
        return new ResponseEntity<>(regulatorUserUpdateDTO, HttpStatus.OK);
    }
    
    @GetMapping(path = "/{userId}/signature")
    @ApiOperation(value = "Generate the token to get the signature of the user with the provided user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileToken.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<FileToken> generateGetRegulatorSignatureToken(
        @PathVariable("userId") @ApiParam(value = "The regulator user id the signature belongs to") @NotNull String userId,
        @RequestParam("signatureUuid") @ApiParam(value = "The signature uuid") @NotNull UUID signatureUuid) {
        FileToken getFileToken =
                userSignatureService.generateSignatureFileToken(userId, signatureUuid);
        return new ResponseEntity<>(getFileToken, HttpStatus.OK);
    }
}
