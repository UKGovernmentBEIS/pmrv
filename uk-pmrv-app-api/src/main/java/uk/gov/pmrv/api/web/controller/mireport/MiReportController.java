package uk.gov.pmrv.api.web.controller.mireport;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportSearchResult;
import uk.gov.pmrv.api.mireport.service.MiReportService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

@RestController
@RequestMapping(path = "/v1.0/mireports")
@RequiredArgsConstructor
@Api(tags = "MiReports")
public class MiReportController {

    private final MiReportService miReportService;

    @GetMapping("types")
    @ApiOperation(value = "Retrieves the mi report types for current user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = List.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<List<MiReportSearchResult>> getCurrentUserMiReports(
        PmrvUser pmrvUser
    ) {
        List<MiReportSearchResult> results =
            miReportService.findByCompetentAuthority(pmrvUser.getCompetentAuthority());
        return ResponseEntity.ok(results);
    }

    @PostMapping
    @ApiOperation(value = "Generates the report identified by the provided report type", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = MiReportResult.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportResult> generateReport(PmrvUser pmrvUser,
                                                         @RequestBody @ApiParam(value = "The parameters based on which the report will be generated", required = true) @Valid MiReportParams reportParams) {
        return ResponseEntity.ok(miReportService.generateReport(pmrvUser, reportParams));
    }

}
