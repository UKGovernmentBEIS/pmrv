package uk.gov.pmrv.api.workflow.request.core.assignment.requestassign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class RequestAssignmentServiceTest {
    
    @InjectMocks
    private RequestAssignmentService service;
    
    @Mock
    private RequestRepository requestRepository;
    
    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Test
    void assignRequestToUser_operator_candidate_assignee() {
        final String operatorAssignee = "operatorAssignee";
        final String candidateAssignee = "candidateAssignee";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .build();
        UserRoleTypeDTO candidateAssigneeRoleType = UserRoleTypeDTO.builder()
            .userId(candidateAssignee)
            .roleType(RoleType.OPERATOR)
            .build();

        when(userRoleTypeService.getUserRoleTypeByUserId(candidateAssignee)).thenReturn(candidateAssigneeRoleType);

        service.assignRequestToUser(request, candidateAssignee);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        Request requestCaptured = requestCaptor.getValue();
        assertEquals(candidateAssignee, requestCaptured.getPayload().getOperatorAssignee());

        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(candidateAssignee);
    }

    @Test
    void assignRequestToUser_candidate_assignee_is_already_operator_assignee() {
        final String operatorAssignee = "operatorAssignee";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .build();
        UserRoleTypeDTO candidateAssigneeRoleType = UserRoleTypeDTO.builder()
            .userId(operatorAssignee)
            .roleType(RoleType.OPERATOR)
            .build();

        when(userRoleTypeService.getUserRoleTypeByUserId(operatorAssignee)).thenReturn(candidateAssigneeRoleType);

        service.assignRequestToUser(request, operatorAssignee);

        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(operatorAssignee);
        verifyNoInteractions(requestRepository);
    }

    @Test
    void assignRequestToUser_regulator_candidate_assignee() {
        final String regulatorAssignee = "regulatorAssignee";
        final String candidateAssignee = "candidateAssignee";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().regulatorAssignee(regulatorAssignee).build())
            .build();
        UserRoleTypeDTO candidateAssigneeRoleType = UserRoleTypeDTO.builder()
            .userId(candidateAssignee)
            .roleType(RoleType.REGULATOR)
            .build();

        when(userRoleTypeService.getUserRoleTypeByUserId(candidateAssignee)).thenReturn(candidateAssigneeRoleType);

        service.assignRequestToUser(request, candidateAssignee);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        Request requestCaptured = requestCaptor.getValue();
        assertEquals(candidateAssignee, requestCaptured.getPayload().getRegulatorAssignee());

        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(candidateAssignee);
    }

    @Test
    void assignRequestToUser_candidate_assignee_is_already_regulator_assignee() {
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().regulatorAssignee(regulatorAssignee).build())
            .build();
        UserRoleTypeDTO candidateAssigneeRoleType = UserRoleTypeDTO.builder()
            .userId(regulatorAssignee)
            .roleType(RoleType.REGULATOR)
            .build();

        when(userRoleTypeService.getUserRoleTypeByUserId(regulatorAssignee)).thenReturn(candidateAssigneeRoleType);

        service.assignRequestToUser(request, regulatorAssignee);

        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(regulatorAssignee);
        verifyNoInteractions(requestRepository);
    }

    @Test
    void assignRequestToUser_candidate_assignee_is_null() {
        final String operatorAssignee = "operatorAssignee";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .build();

        BusinessException businessException =
            assertThrows(BusinessException.class, () -> service.assignRequestToUser(request, null));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());
        verifyNoInteractions(userRoleTypeService, requestRepository);
    }

    @Test
    void assignRequestToUser_verifier_candidate_assignee() {
        final String regulatorAssignee = "regulatorAssignee";
        final String candidateAssignee = "candidateAssignee";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().regulatorAssignee(regulatorAssignee).build())
            .build();
        UserRoleTypeDTO candidateAssigneeRoleType = UserRoleTypeDTO.builder()
            .userId(candidateAssignee)
            .roleType(RoleType.VERIFIER)
            .build();

        when(userRoleTypeService.getUserRoleTypeByUserId(candidateAssignee)).thenReturn(candidateAssigneeRoleType);

        service.assignRequestToUser(request, candidateAssignee);

        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(candidateAssignee);
        verifyNoInteractions(requestRepository);
    }
}
