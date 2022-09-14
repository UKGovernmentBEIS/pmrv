package uk.gov.pmrv.api.mireport.service.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestActionsMiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;
import uk.gov.pmrv.api.mireport.repository.ExecutedRequestActionsRepository;

@Service
@RequiredArgsConstructor
public class ExecutedRequestActionsReportGenerator implements MiReportGenerator {

    private final ExecutedRequestActionsRepository executedRequestActionsRepository;

    @Override
    public MiReportResult generateMiReport(CompetentAuthority competentAuthority, MiReportParams reportParams) {
        return ExecutedRequestActionsMiReportResult.builder()
            .reportType(getReportType())
            .actions(executedRequestActionsRepository
                .findRequestActionsByCaAndParams(competentAuthority, (ExecutedRequestActionsMiReportParams) reportParams)
            )
            .build();
    }

    @Override
    public MiReportType getReportType() {
        return MiReportType.COMPLETED_WORK;
    }
}
