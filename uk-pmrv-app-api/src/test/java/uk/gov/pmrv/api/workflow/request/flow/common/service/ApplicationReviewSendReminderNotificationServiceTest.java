package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

@ExtendWith(MockitoExtension.class)
class ApplicationReviewSendReminderNotificationServiceTest {
    
    @InjectMocks
    private ApplicationReviewSendReminderNotificationService service;
    
    @Mock
    private RequestService requestService;
    
    @Mock
    private UserAuthService userAuthService;
    
    @Mock
    private RequestExpirationReminderService requestExpirationReminderService;

    @Test
    void sendFirstReminderNotification() {
        String requestId = "1";
        Date deadline = new Date();
        RequestType requestType = RequestType.PERMIT_SURRENDER;
        String regulatorAssignee = "reg assignee";
        
        Request request = Request.builder().id(requestId)
                .type(requestType)
                .payload(PermitSurrenderRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .build())
                .build();
        
        UserInfoDTO regulatorAssigneeUser = UserInfoDTO.builder().firstName("fn").lastName("ln").email("email@email").build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(userAuthService.getUserByUserId(regulatorAssignee)).thenReturn(regulatorAssigneeUser);
        
        // invoke
        service.sendFirstReminderNotification(requestId, deadline);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(userAuthService, times(1)).getUserByUserId(regulatorAssignee);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId, 
                NotificationTemplateExpirationReminderParams
                .builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW.getDescription())
                .recipient(regulatorAssigneeUser)
                .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build()
               );
    }
    
    @Test
    void sendFirstReminderNotification_no_assignee_exist() {
        String requestId = "1";
        Date deadline = new Date();
        RequestType requestType = RequestType.PERMIT_SURRENDER;
        String regulatorAssignee = null;
        
        Request request = Request.builder().id(requestId)
                .type(requestType)
                .payload(PermitSurrenderRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .build())
                .build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        
        // invoke
        service.sendFirstReminderNotification(requestId, deadline);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(userAuthService, requestExpirationReminderService);
    }
    
    @Test
    void sendSecondReminderNotification() {
        String requestId = "1";
        Date deadline = new Date();
        RequestType requestType = RequestType.PERMIT_SURRENDER;
        String regulatorAssignee = "reg assignee";
        
        Request request = Request.builder().id(requestId)
                .type(requestType)
                .payload(PermitSurrenderRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .build())
                .build();
        
        UserInfoDTO regulatorAssigneeUser = UserInfoDTO.builder().firstName("fn").lastName("ln").email("email@email").build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(userAuthService.getUserByUserId(regulatorAssignee)).thenReturn(regulatorAssigneeUser);
        
        // invoke
        service.sendSecondReminderNotification(requestId, deadline);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(userAuthService, times(1)).getUserByUserId(regulatorAssignee);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId, 
                NotificationTemplateExpirationReminderParams
                .builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW.getDescription())
                .recipient(regulatorAssigneeUser)
                .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build()
               );
    }
}
