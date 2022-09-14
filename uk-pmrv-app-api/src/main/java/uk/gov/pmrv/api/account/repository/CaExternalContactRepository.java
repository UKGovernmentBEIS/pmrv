package uk.gov.pmrv.api.account.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

public interface CaExternalContactRepository extends JpaRepository<CaExternalContact, Long> {

    List<CaExternalContact> findByCompetentAuthority(CompetentAuthority ca);
    
    List<CaExternalContact> findAllByIdIn(Set<Long> ids);

    Optional<CaExternalContact> findByIdAndCompetentAuthority(Long id, CompetentAuthority ca);

    boolean existsByCompetentAuthorityAndName(CompetentAuthority ca, String name);

    boolean existsByCompetentAuthorityAndEmail(CompetentAuthority ca, String email);

    boolean existsByCompetentAuthorityAndNameAndIdNot(CompetentAuthority ca, String name, Long id);

    boolean existsByCompetentAuthorityAndEmailAndIdNot(CompetentAuthority ca, String email, Long id);

    void deleteById(Long id);
}
