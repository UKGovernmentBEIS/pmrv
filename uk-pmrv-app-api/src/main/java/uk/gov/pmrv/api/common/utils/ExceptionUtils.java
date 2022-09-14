package uk.gov.pmrv.api.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtils {

    public Throwable getRootCause(Throwable e) {
        Throwable cause;
        Throwable result = e;

        while (null != (cause = result.getCause()) 
                && (result != cause)) {
            result = cause;
        }
        return result;
    }
}
