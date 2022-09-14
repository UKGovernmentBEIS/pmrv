package uk.gov.pmrv.api.common.exception;

import lombok.NoArgsConstructor;

/**
 * Custom business logic checked exception.
 */
@NoArgsConstructor
public class BusinessCheckedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@link BusinessCheckedException}. with the specified detail message.
     * @param message the detailed message.
     */
    public BusinessCheckedException(String message) {
        super(message);
    }
}
