package uk.gov.pmrv.api.common.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class DateService {

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }
}
