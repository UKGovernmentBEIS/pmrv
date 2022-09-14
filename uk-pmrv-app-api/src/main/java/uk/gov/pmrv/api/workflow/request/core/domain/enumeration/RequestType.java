package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Request Types.
 */
@Getter
@AllArgsConstructor
public enum RequestType {

    INSTALLATION_ACCOUNT_OPENING("PROCESS_INSTALLATION_ACCOUNT_OPENING", "Account creation", RequestCategory.PERMIT),
    SYSTEM_MESSAGE_NOTIFICATION("SYSTEM_MESSAGE_NOTIFICATION", "System Message Notification", null),
    PERMIT_ISSUANCE("PROCESS_PERMIT_ISSUANCE", "Permit Application", RequestCategory.PERMIT),
    PERMIT_SURRENDER("PROCESS_PERMIT_SURRENDER", "Permit Surrender", RequestCategory.PERMIT),
    PERMIT_REVOCATION("PROCESS_PERMIT_REVOCATION", "Permit Revocation", RequestCategory.PERMIT),
    PERMIT_TRANSFER("PROCESS_PERMIT_TRANSFER", "Permit Transfer", RequestCategory.PERMIT),
    PERMIT_VARIATION("PROCESS_PERMIT_VARIATION", "Permit Variation", RequestCategory.PERMIT),
    PERMIT_NOTIFICATION("PROCESS_PERMIT_NOTIFICATION", "Permit Notification", RequestCategory.PERMIT),
    AER("PROCESS_AER", "AER", RequestCategory.REPORTING),
    
    // The following is not a request type as the above, in the sense that there is no request entity that carries this
    // value in the type. It is needed though in order for the dynamic tasks (review, amends etc) to be created. 
    PERMIT_NOTIFICATION_FOLLOW_UP("PROCESS_PERMIT_NOTIFICATION_FOLLOW_UP", "Notification follow-up", null),
    ;

    /**
     * The id of the bpmn process that will be instantiated for this request type.
     */
    private final String processDefinitionId;

    /**
     * The description of the request type.
     */
    private final String description;

    private final RequestCategory category;

    public static Set<RequestType> getRequestTypesByCategory(RequestCategory category) {
        return getRequestTypesByCategory(category, Arrays.stream(RequestType.values()).collect(Collectors.toSet()));
    }

    public static Set<RequestType> getRequestTypesByCategory(RequestCategory category, Set<RequestType> requestTypes) {
        return requestTypes.stream()
                .filter(type -> type.getCategory() != null && type.getCategory().equals(category))
                .collect(Collectors.toSet());
    }
}
