package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;

import org.springframework.stereotype.Repository;

import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class ItemOperatorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemPage findItemsByRequestId(Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes, String requestId) {
        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;

        JPAQuery<Item> query = new JPAQuery<>(entityManager);

        BooleanBuilder hasPermissionOnTask = new BooleanBuilder();
        scopedRequestTaskTypes.forEach((accountId, types) -> hasPermissionOnTask.or(request.accountId.eq(accountId).and(requestTask.type.in(types))));

        JPAQuery<Item> jpaQuery = query.select(
                Projections.constructor(Item.class,
                        requestTask.startDate,
                        request.id, request.type, request.accountId,
                        requestTask.id, requestTask.type, requestTask.assignee,
                        requestTask.dueDate, requestTask.pauseDate, Expressions.FALSE))
                .from(request)
                .innerJoin(requestTask)
                .on(request.id.eq(requestTask.request.id))
                .where(request.id.eq(requestId)
                        .and(hasPermissionOnTask)
                ).orderBy(requestTask.startDate.desc());

        List<Item> items = jpaQuery.fetch();
        return ItemPage.builder()
                .items(items)
                .totalItems((long) items.size())
                .build();
    }
}
