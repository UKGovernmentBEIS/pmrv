package uk.gov.pmrv.api.files.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.InputStream;

@Log4j2
@Service
@RequiredArgsConstructor
class ClamAVClientService implements FileScanService {

    private final ClamavClient clamavClient;

    public void scan(InputStream is) {
        ScanResult res = clamavClient.scan(is);
        if (res instanceof ScanResult.VirusFound) {
            log.error("The selected file contains a virus");
            throw new BusinessException(ErrorCode.INFECTED_STREAM);
        }
    }
}
