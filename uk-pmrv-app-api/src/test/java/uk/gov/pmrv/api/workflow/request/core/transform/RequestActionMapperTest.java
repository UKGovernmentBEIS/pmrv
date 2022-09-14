package uk.gov.pmrv.api.workflow.request.core.transform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;

class RequestActionMapperTest {

    private RequestActionMapper mapper;
    
    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RequestActionMapper.class);
    }
    
    @Test
    void toRequestActionDTO() {
        Long accountId = 100L;
        Request request = Request.builder().accountId(accountId).build();
        InstallationAccountOpeningApplicationSubmittedRequestActionPayload requestActionPayload =
            InstallationAccountOpeningApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD)
                .accountPayload(AccountPayload.builder().name("accountName").build())
                .build();
        RequestAction requestAction = RequestAction.builder()
            .id(1L)
            .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
            .submitter("fn ln")
            .payload(requestActionPayload)
            .request(request)
            .build();
        
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
        assertThat(result.getPayload()).isEqualTo(requestActionPayload);
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
    }
    
    @Test
    void toRequestActionInfoDTO() {
        RequestAction requestAction = RequestAction.builder()
                .id(1L)
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
                .submitter("fn ln")
                .build();
        
        RequestActionInfoDTO result = mapper.toRequestActionInfoDTO(requestAction);
        assertThat(result.getType()).isEqualTo(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
    }
    
    @Test
    void toRequestActionDTOIgnorePayload() {
        Long accountId = 100L;
        Request request = Request.builder().accountId(accountId).build();
        RequestAction requestAction = RequestAction.builder()
                .id(1L)
                .payload(PermitSurrenderApplicationRejectedRequestActionPayload.builder().payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD).build())
                .type(RequestActionType.PERMIT_SURRENDER_APPLICATION_REJECTED)
                .submitter("fn ln")
                .request(request)
                .build();
        
        RequestActionDTO result = mapper.toRequestActionDTOIgnorePayload(requestAction);
        assertThat(result.getPayload()).isNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
    }
}
