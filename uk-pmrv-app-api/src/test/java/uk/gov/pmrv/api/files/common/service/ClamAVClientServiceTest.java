package uk.gov.pmrv.api.files.common.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static xyz.capybara.clamav.commands.scan.result.ScanResult.OK;
import static xyz.capybara.clamav.commands.scan.result.ScanResult.VirusFound;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import xyz.capybara.clamav.ClamavClient;


@ExtendWith(MockitoExtension.class)
class ClamAVClientServiceTest {

    @InjectMocks
    ClamAVClientService service;

    @Mock
    ClamavClient clamavClient;

    @Test
    void scan_clean() {
        InputStream is = new ByteArrayInputStream("clean".getBytes());
        when(clamavClient.scan(is)).thenReturn(OK.INSTANCE);
        assertDoesNotThrow(() -> service.scan(is));
    }

    @Test
    void scan_infected() {
        InputStream is = new ByteArrayInputStream("infected".getBytes());
        when(clamavClient.scan(is)).thenReturn(new VirusFound(Map.of()));
        BusinessException businessException = assertThrows(BusinessException.class, () -> service.scan(is));
        assertEquals(ErrorCode.INFECTED_STREAM, businessException.getErrorCode());
    }
}
