package uk.gov.pmrv.api.mireport.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.account.domain.QAccount;
import uk.gov.pmrv.api.account.domain.QLegalEntity;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestAction;
import uk.gov.pmrv.api.mireport.domain.dto.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.permit.domain.QPermitEntity;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestAction;

@Repository
public class ExecutedRequestActionsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ExecutedRequestAction> findRequestActionsByCaAndParams(CompetentAuthority competentAuthority,
                                                                       ExecutedRequestActionsMiReportParams reportParams) {
        QRequest request = QRequest.request;
        QRequestAction requestAction = QRequestAction.requestAction;
        QAccount account = QAccount.account;
        QLegalEntity legalEntity = QLegalEntity.legalEntity;
        QPermitEntity permit = QPermitEntity.permitEntity;

        JPAQuery<ExecutedRequestAction> query = new JPAQuery<>(entityManager);

        BooleanBuilder isCreationDateBeforeToDate = new BooleanBuilder();
        if(reportParams.getToDate() != null){
            isCreationDateBeforeToDate.and(requestAction.creationDate.before(LocalDateTime.of(reportParams.getToDate(), LocalTime.MIDNIGHT)));
        }

        JPAQuery<ExecutedRequestAction> jpaQuery = query.select(
            Projections.constructor(ExecutedRequestAction.class,
                account.id, account.accountType, account.name, account.status,
                legalEntity.name, permit.id,
                request.id, request.type, request.status,
                requestAction.type, requestAction.submitter, requestAction.creationDate))
            .from(request)
            .innerJoin(requestAction).on(request.id.eq(requestAction.request.id))
            .innerJoin(account).on(request.accountId.eq(account.id))
            .innerJoin(legalEntity).on(account.legalEntity.id.eq(legalEntity.id))
            .leftJoin(permit).on(account.id.eq(permit.accountId))
            .where(request.competentAuthority.eq(competentAuthority)
                    .and(requestAction.creationDate.goe(LocalDateTime.of(reportParams.getFromDate(), LocalTime.MIDNIGHT)))
                    .and(isCreationDateBeforeToDate)
            )
            .orderBy(account.id.asc(),request.type.asc(), request.id.asc(), requestAction.creationDate.asc());

        return jpaQuery.fetch();
    }
}
