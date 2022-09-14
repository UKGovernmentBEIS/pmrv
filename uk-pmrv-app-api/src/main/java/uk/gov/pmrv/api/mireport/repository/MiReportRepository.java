package uk.gov.pmrv.api.mireport.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.domain.MiReportEntity;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportSearchResult;

@Repository
public interface MiReportRepository extends JpaRepository<MiReportEntity, Long> {


    @Transactional(readOnly = true)
    List<MiReportSearchResult> findByCompetentAuthority(CompetentAuthority competentAuthority);
}
