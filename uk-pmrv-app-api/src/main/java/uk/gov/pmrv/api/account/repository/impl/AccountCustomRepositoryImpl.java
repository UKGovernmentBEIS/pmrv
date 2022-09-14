package uk.gov.pmrv.api.account.repository.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.repository.AccountCustomRepository;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class AccountCustomRepositoryImpl implements AccountCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public AccountSearchResults findByAccountIds(List<Long> accountIds, AccountSearchCriteria searchCriteria) {
        return AccountSearchResults.builder()
            .accounts(constructQuery(accountIds, null, searchCriteria, false).getResultList())
            .total(((Number) constructQuery(accountIds, null, searchCriteria, true).getSingleResult()).longValue())
            .build();
    }

    @Override
    public AccountSearchResults findByCompAuth(CompetentAuthority compAuth, AccountSearchCriteria searchCriteria) {
        return AccountSearchResults.builder()
            .accounts(constructQuery(null, compAuth, searchCriteria, false).getResultList())
            .total(((Number) constructQuery(null, compAuth, searchCriteria, true).getSingleResult()).longValue())
            .build();
    }

    private Query constructQuery(List<Long> accountIds, CompetentAuthority compAuth,
                                 AccountSearchCriteria searchCriteria, boolean forCount) {
        StringBuilder sb = new StringBuilder();

        if (forCount) {
            sb.append("select count(*) from ( \n");
        }

        sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc.status, le.name as legalEntityName \n")
            .append("from account acc \n")
            .append("join account_legal_entity le on le.id = acc.legal_entity_id \n")
            .append("where 1 = 1 \n")
        ;

        constructMainWhereClause(accountIds, compAuth, searchCriteria, sb);

        if (StringUtils.hasText(searchCriteria.getTerm())) {
            sb.append("and (acc.name ilike :accName \n");
            sb.append("or acc.site_name ilike :accSiteName \n");
            sb.append("or acc.emitter_id ilike :accEmitterId \n");
            sb.append(") \n");

            sb.append("UNION \n");

            sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc.status, le.name as legalEntityName \n")
                .append("from account_legal_entity le \n")
                .append("join account acc on le.id = acc.legal_entity_id \n")
                .append("where le.name ilike :leName \n");

            constructMainWhereClause(accountIds, compAuth, searchCriteria, sb);

            sb.append("UNION \n");

            sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc.status, le.name as legalEntityName \n")
                .append("from account_search_additional_keyword ak \n")
                .append("join account acc on ak.account_id = acc.id \n")
                .append("join account_legal_entity le on le.id = acc.legal_entity_id \n")
                .append("where ak.value ilike :keywordValue \n");

            constructMainWhereClause(accountIds, compAuth, searchCriteria, sb);

        }

        if (!forCount) {
            sb.append("order by emitterId asc \n")
                .append("limit :limit \n")
                .append("offset :offset \n");
        } else {
            sb.append(") results");
        }

        Query query;
        if (forCount) {
            query = entityManager.createNativeQuery(sb.toString());
        } else {
            query = entityManager.createNativeQuery(sb.toString(), Account.ACCOUNT_INFO_DTO_RESULT_MAPPER);
        }

        populateMainWhereClauseParameters(accountIds, compAuth, searchCriteria, query);

        if (StringUtils.hasText(searchCriteria.getTerm())) {
            query.setParameter("accName", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("accSiteName", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("accEmitterId", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("leName", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("keywordValue", "%" + searchCriteria.getTerm() + "%");

            populateMainWhereClauseParameters(accountIds, compAuth, searchCriteria, query);
        }

        if (!forCount) {
            query.setParameter("limit", searchCriteria.getPageSize());
            query.setParameter("offset", searchCriteria.getPage() * searchCriteria.getPageSize());
        }

        return query;
    }

    private void constructMainWhereClause(List<Long> accountIds, CompetentAuthority compAuth,
                                          AccountSearchCriteria searchCriteria, StringBuilder sb) {
        if (ObjectUtils.isNotEmpty(accountIds)) {
            sb.append("and acc.id in :accountIds \n");
        }

        if (compAuth != null) {
            sb.append("and acc.competent_authority = :compAuth \n");
        }

        if (searchCriteria.getType() != null) {
            sb.append("and acc.type = :accType \n");
        }
    }

    private void populateMainWhereClauseParameters(List<Long> accountIds, CompetentAuthority compAuth,
                                                   AccountSearchCriteria searchCriteria, Query query) {
        if (ObjectUtils.isNotEmpty(accountIds)) {
            query.setParameter("accountIds", accountIds);
        }

        if (compAuth != null) {
            query.setParameter("compAuth", compAuth.name());
        }

        if (searchCriteria.getType() != null) {
            query.setParameter("accType", searchCriteria.getType().name());
        }
    }

}
