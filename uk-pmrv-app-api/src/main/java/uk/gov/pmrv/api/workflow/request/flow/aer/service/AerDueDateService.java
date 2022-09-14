package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AerDueDateService {

    public Date generateDueDate() {
        // For all AERs generated automatically the deadline is set at 01/04
        LocalDate deadline = LocalDate.of(Year.now().getValue(), 4, 1);
        return DateUtils.convertLocalDateToDate(deadline);
    }
}
