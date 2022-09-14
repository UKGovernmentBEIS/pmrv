package uk.gov.pmrv.api.workflow.request.flow.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExpirationReminderType {

    FIRST_REMINDER(7, "1 week", "in one week"),
    SECOND_REMINDER(1, "1 day", "tomorrow")
    ;
    
    private int daysToExpire;
    private String description;
    private String descriptionLong;
}
