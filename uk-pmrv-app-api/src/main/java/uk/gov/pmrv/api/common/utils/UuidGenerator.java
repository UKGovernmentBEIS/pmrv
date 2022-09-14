package uk.gov.pmrv.api.common.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public final class UuidGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }
}
