package uk.gov.pmrv.api.mireport.service.generator;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;

public interface MiReportGenerator {

    MiReportResult generateMiReport(CompetentAuthority competentAuthority, MiReportParams reportParams);

    MiReportType getReportType();
}
