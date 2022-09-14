package uk.gov.pmrv.api.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

class ExceptionUtilsTest {

    @Test
    void getRootCause() {
        RuntimeException rootCause = new RuntimeException("user is disabled");
        BusinessException be = new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED, rootCause, new Object(){});
        Throwable result = ExceptionUtils.getRootCause(be);
        assertThat(result).isEqualTo(rootCause);
    }
    
    @Test
    void getRootCause_no_cause_exist_returns_itself() {
        BusinessException be = new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED, new Object(){});
        Throwable result = ExceptionUtils.getRootCause(be);
        assertThat(result).isEqualTo(be);
    }
}
