package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the various decision outcomes
 */
@Getter
@AllArgsConstructor
public enum Decision {

    ACCEPTED("Accepted"),
    REJECTED("Rejected")
    ;

    /**
     * The name.
     */
    private final String name;
}
