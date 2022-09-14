package uk.gov.pmrv.api.notification.template.repository.impl;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.repository.DocumentTemplateCustomRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class DocumentTemplateCustomRepositoryImpl implements DocumentTemplateCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TemplateSearchResults findByCompetentAuthorityAndSearchCriteria(CompetentAuthority competentAuthority,
                                                                           DocumentTemplateSearchCriteria searchCriteria) {
        return TemplateSearchResults.builder()
            .templates(constructResultsQuery(competentAuthority, searchCriteria).getResultList())
            .total(((Number) constructCountQuery(competentAuthority, searchCriteria).getSingleResult()).longValue())
            .build();
    }

    private Query constructResultsQuery(CompetentAuthority competentAuthority, DocumentTemplateSearchCriteria searchCriteria) {
        StringBuilder sb = new StringBuilder();

        sb.append(constructMainQueryStatement(searchCriteria))
            .append("order by name asc \n")
            .append("limit :limit \n")
            .append("offset :offset \n");

        return createQuery(sb.toString(), competentAuthority, searchCriteria, false);
    }

    private Query constructCountQuery(CompetentAuthority competentAuthority, DocumentTemplateSearchCriteria searchCriteria) {
        StringBuilder sb = new StringBuilder();

        sb.append("select count(*) from ( \n")
            .append(constructMainQueryStatement(searchCriteria))
            .append(") results");

        return createQuery(sb.toString(), competentAuthority, searchCriteria, true);
    }

    private String constructMainQueryStatement(DocumentTemplateSearchCriteria searchCriteria) {
        StringBuilder sb = new StringBuilder();

        sb.append("select id, name, operator_type as operatorType, workflow, last_updated_date as lastUpdatedDate \n")
            .append("from notification_document_template \n")
            .append("where competent_authority = :competentAuthority \n")
            .append(StringUtils.hasText(searchCriteria.getTerm()) ? "and (name ilike :term or workflow ilike :term) \n" : "\n");

        return sb.toString();
    }

    private Query createQuery(String sqlStatement, CompetentAuthority competentAuthority,
                              DocumentTemplateSearchCriteria searchCriteria, boolean forCount) {
        Query query = forCount
            ? entityManager.createNativeQuery(sqlStatement)
            : entityManager.createNativeQuery(sqlStatement, DocumentTemplate.DOCUMENT_TEMPLATE_INFO_DTO_RESULT_MAPPER);

        query.setParameter("competentAuthority", competentAuthority.name());

        if(StringUtils.hasText(searchCriteria.getTerm())) {
            query.setParameter("term", "%" + searchCriteria.getTerm() + "%");
        }

        if(!forCount) {
            query.setParameter("limit", searchCriteria.getPageSize());
            query.setParameter("offset", searchCriteria.getPage() * searchCriteria.getPageSize());
        }

        return query;
    }
}
