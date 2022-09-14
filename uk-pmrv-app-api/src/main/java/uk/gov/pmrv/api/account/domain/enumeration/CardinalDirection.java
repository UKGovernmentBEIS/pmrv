package uk.gov.pmrv.api.account.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Enumerates the cardinal directions.
 */
@Getter
@AllArgsConstructor
public enum CardinalDirection {

    EAST("East"),
    NORTH("North"),
    SOUTH("South"),
    WEST("West");

    /** The name */
    private final String name;

    /**
     * Returns enumerator from character.
     *
     * @param firstLetter First letter of cardinal direction
     * @return {@link CardinalDirection}
     */
    public static CardinalDirection findCardinalDirection(char firstLetter) {
        return Arrays.stream(CardinalDirection.values())
                .filter(direction -> direction.getName().charAt(0) == firstLetter)
                .findFirst().orElse(null);
    }
}
