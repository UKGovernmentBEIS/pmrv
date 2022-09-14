package uk.gov.pmrv.api.authorization.regulator.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class RegulatorUserStatusDisabledEvent {
    private String userId;
    
    public RegulatorUserStatusDisabledEvent(String userId) {
        this.userId = userId;
    }
}
