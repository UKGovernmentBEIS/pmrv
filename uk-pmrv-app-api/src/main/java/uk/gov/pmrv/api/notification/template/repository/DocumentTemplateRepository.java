package uk.gov.pmrv.api.notification.template.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long>, DocumentTemplateCustomRepository {
    
    Optional<DocumentTemplate> findByTypeAndCompetentAuthority(DocumentTemplateType type, CompetentAuthority competentAuthority);
}
