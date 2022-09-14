package uk.gov.pmrv.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestActionAuthorityInfoDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;

@ExtendWith(MockitoExtension.class)
class RequestActionAuthorityInfoQueryServiceTest {

    @InjectMocks
    private RequestActionAuthorityInfoQueryService service;

    @Mock
    private RequestActionRepository requestActionRepository;

    @Test
    void getRequestActionAuthorityInfo() {
        Long requestActionId = 1L;
        Request request = Request.builder()
            .accountId(1L)
            .competentAuthority(ENGLAND)
            .verificationBodyId(2L)
            .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
            .build();

        RequestAction requestAction = RequestAction.builder()
            .id(requestActionId)
            .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
            .request(request)
            .build();

        when(requestActionRepository.findById(requestActionId)).thenReturn(Optional.of(requestAction));

        RequestActionAuthorityInfoDTO requestActionInfo = service.getRequestActionAuthorityInfo(requestActionId);

        assertThat(requestActionInfo.getId()).isEqualTo(requestActionId);
        assertThat(requestActionInfo.getType()).isEqualTo(requestAction.getType().name());
        assertEquals(request.getAccountId(), requestActionInfo.getAuthorityInfo().getAccountId());
        assertEquals(request.getCompetentAuthority(), requestActionInfo.getAuthorityInfo().getCompetentAuthority());
        assertEquals(request.getVerificationBodyId(), requestActionInfo.getAuthorityInfo().getVerificationBodyId());
    }

    @Test
    void getRequestActionAuthorityInfo_not_exists() {
        Long requestActionId = 1L;
        when(requestActionRepository.findById(requestActionId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.getRequestActionAuthorityInfo(requestActionId));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
    }
}