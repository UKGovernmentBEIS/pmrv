package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationTemplateWorkflowTaskType {

    RFI("Request for Information"),
    RDE("Request for Determination"),
    
    // RequestType + _ + SubRequestType
    PERMIT_SURRENDER_APPLICATION_REVIEW("Determination"),
    PERMIT_NOTIFICATION_APPLICATION_REVIEW("Determination"),
    PERMIT_ISSUANCE_APPLICATION_REVIEW("Determination"),
    
    PERMIT_NOTIFICATION_FOLLOW_UP("Operator Response"),
        
    PAYMENT("Payment"),
    ;
    
    private String description;
    
    public static NotificationTemplateWorkflowTaskType fromString(String type) {
        return Stream.of(values())
                .filter(workflowTaskType -> workflowTaskType.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + type));
    }
}
