package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DynamicUserTaskDefinitionKey {

    APPLICATION_REVIEW("APPLICATION_REVIEW"),
    WAIT_FOR_REVIEW("WAIT_FOR_REVIEW"),
    APPLICATION_PEER_REVIEW("APPLICATION_PEER_REVIEW"),
    WAIT_FOR_PEER_REVIEW("WAIT_FOR_PEER_REVIEW"),
    WAIT_FOR_AMENDS("WAIT_FOR_AMENDS"),
    APPLICATION_AMENDS_SUBMIT("APPLICATION_AMENDS_SUBMIT"),
    SYSTEM_MESSAGE_NOTIFICATION_TASK("SYSTEM_MESSAGE_NOTIFICATION_TASK"),
    RFI_RESPONSE_SUBMIT("RFI_RESPONSE_SUBMIT"),
    WAIT_FOR_RFI_RESPONSE("WAIT_FOR_RFI_RESPONSE"),
    RDE_RESPONSE_SUBMIT("RDE_RESPONSE_SUBMIT"),
    WAIT_FOR_RDE_RESPONSE("WAIT_FOR_RDE_RESPONSE"),
    MAKE_PAYMENT("MAKE_PAYMENT"),
    TRACK_PAYMENT("TRACK_PAYMENT"),
    CONFIRM_PAYMENT("CONFIRM_PAYMENT"),
    FOLLOW_UP("FOLLOW_UP"),
    WAIT_FOR_FOLLOW_UP("WAIT_FOR_FOLLOW_UP"),
    AER_APPLICATION_SUBMIT("AER_APPLICATION_SUBMIT"),
    ;

    private String id;
}
