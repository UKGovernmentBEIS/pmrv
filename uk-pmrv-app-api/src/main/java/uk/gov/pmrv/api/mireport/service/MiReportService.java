package uk.gov.pmrv.api.mireport.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportSearchResult;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;
import uk.gov.pmrv.api.mireport.repository.MiReportRepository;
import uk.gov.pmrv.api.mireport.service.generator.MiReportGenerator;

@Service
@RequiredArgsConstructor
public class MiReportService {

    private final MiReportRepository miReportRepository;
    private final List<MiReportGenerator> miReportGenerators;

    public List<MiReportSearchResult> findByCompetentAuthority(CompetentAuthority competentAuthority) {
        return miReportRepository.findByCompetentAuthority(competentAuthority);
    }

    @Transactional(readOnly = true)
    public MiReportResult generateReport(PmrvUser pmrvUser, MiReportParams reportParams) {
        return miReportGenerators.stream()
            .filter(generator -> isReportTypeFound(reportParams.getReportType(), generator))
            .findFirst()
            .map(generator -> generator.generateMiReport(pmrvUser.getCompetentAuthority(), reportParams))
            .orElseThrow(() -> new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED));
    }

    private boolean isReportTypeFound(MiReportType miReportType, MiReportGenerator generator) {
        return generator.getReportType() == miReportType;
    }
}
