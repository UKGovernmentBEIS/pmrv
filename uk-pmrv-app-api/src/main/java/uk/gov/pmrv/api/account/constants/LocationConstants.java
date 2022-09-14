package uk.gov.pmrv.api.account.constants;

import lombok.experimental.UtilityClass;

/**
 * Pattern Format Constants.
 */
@UtilityClass
public final class LocationConstants {

    /** The coordinates format for DB store as Degrees Minutes Second Direction */
    public static final String COORDINATES_FORMAT = "%d %d %.2f %c";
}
