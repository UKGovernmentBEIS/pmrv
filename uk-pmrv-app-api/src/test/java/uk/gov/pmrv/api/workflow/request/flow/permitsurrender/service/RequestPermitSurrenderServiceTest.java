package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper.PermitSurrenderMapper;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestPermitSurrenderServiceTest {

    @InjectMocks
    private RequestPermitSurrenderService service;
    
    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitSurrenderSubmitValidatorService permitSurrenderSubmitValidatorService;
    
    @Test
    void applySavePayload_existing_task_payload() {
        PermitSurrenderApplicationSubmitRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD)
                        .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).build())
                        .sectionsCompleted(Map.of("SECTION_1", true))
                        .build();
        
        PermitSurrenderSaveApplicationRequestTaskActionPayload actionPayload = 
                PermitSurrenderSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD)
                        .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justify").build())
                        .sectionsCompleted(Map.of())
                        .build();
        
        RequestTask requestTask = RequestTask.builder().id(1L).payload(taskPayload).type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_SUBMIT).build();
        
        service.applySavePayload(actionPayload, requestTask);
        
        assertThat(taskPayload.getPermitSurrender()).isEqualTo(actionPayload.getPermitSurrender());
        assertThat(taskPayload.getSectionsCompleted()).isEmpty();
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }
    
    @Test
    void applySavePayload_new_task_payload() {
        PermitSurrenderApplicationSubmitRequestTaskPayload taskPayload = null;
        
        PermitSurrenderSaveApplicationRequestTaskActionPayload actionPayload = 
                PermitSurrenderSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD)
                        .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).build())
                        .sectionsCompleted(Map.of("SECTION_1", true))
                        .build();
        
        RequestTask requestTask = RequestTask.builder().id(1L).payload(taskPayload).type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_SUBMIT).build();
        
        service.applySavePayload(actionPayload, requestTask);
        
        PermitSurrenderApplicationSubmitRequestTaskPayload savedTaskPayload = (PermitSurrenderApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        
        assertThat(savedTaskPayload.getPermitSurrender()).isEqualTo(actionPayload.getPermitSurrender());
        assertThat(savedTaskPayload.getSectionsCompleted()).isEqualTo(actionPayload.getSectionsCompleted());
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }
    
    @Test
    void applySubmitPayload() {
        PmrvUser authUser = PmrvUser.builder().userId("user").build();
        
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder().payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD).build();
        Request request = Request.builder().id("1").payload(requestPayload).type(RequestType.PERMIT_SURRENDER).build();
        
        PermitSurrenderApplicationSubmitRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD)
                        .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).documentsExist(false).build())
                        .sectionsCompleted(Map.of("SECTION_1", true))
                        .build();
        RequestTask requestTask = RequestTask.builder().id(1L)
                .payload(taskPayload)
                .type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_SUBMIT)
                .request(request)
                .build();
        
        RequestActionPayload actionPayload = Mappers.getMapper(PermitSurrenderMapper.class).toApplicationSubmittedRequestActionPayload(taskPayload);
        
        service.applySubmitPayload(requestTask, authUser);

        verify(permitSurrenderSubmitValidatorService, times(1)).validatePermitSurrender(
                PermitSurrenderContainer.builder().permitSurrender(taskPayload.getPermitSurrender()).build());
        verify(requestService, times(1)).saveRequest(request);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, RequestActionType.PERMIT_SURRENDER_APPLICATION_SUBMITTED, "user");
    }
}
