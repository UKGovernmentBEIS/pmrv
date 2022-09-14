package uk.gov.pmrv.api.web.controller.workflow;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_PROCESS_EXISTING_CARD_PAYMENT_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_CREATE_CARD_PAYMENT_BAD_REQUEST;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType.PAYMENT_PAY_BY_CARD;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentCreateResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.CardPaymentService;

@RestController
@RequestMapping(path = "/v1.0/tasks-payment")
@RequiredArgsConstructor
@Api(tags = "Payments")
public class RequestTaskPaymentController {

    private final CardPaymentService cardPaymentService;

    @PostMapping(path = "/{taskId}/create")
    @ApiOperation(value = "Create card payment for the provided task", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK),
        @ApiResponse(code = 400, message = REQUEST_TASK_CREATE_CARD_PAYMENT_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<CardPaymentCreateResponseDTO> createCardPayment(PmrvUser pmrvUser,
                                                                          @PathVariable("taskId") @ApiParam(value = "The task id") Long taskId) {
        return ResponseEntity.ok(cardPaymentService.createCardPayment(taskId, PAYMENT_PAY_BY_CARD, pmrvUser));
    }

    @PostMapping(path = "/{taskId}/process")
    @ApiOperation(value = "Process existing card payment that corresponds to the provided task", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK),
        @ApiResponse(code = 400, message = REQUEST_TASK_PROCESS_EXISTING_CARD_PAYMENT_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<CardPaymentProcessResponseDTO> processExistingCardPayment(PmrvUser pmrvUser,
                                                                            @PathVariable("taskId") @ApiParam(value = "The task id") Long taskId) {
        return ResponseEntity.ok(cardPaymentService.processExistingCardPayment(taskId, PAYMENT_PAY_BY_CARD, pmrvUser));
    }
}
