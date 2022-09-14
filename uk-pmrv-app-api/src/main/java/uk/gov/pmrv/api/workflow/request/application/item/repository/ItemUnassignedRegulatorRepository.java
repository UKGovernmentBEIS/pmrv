package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.QRequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class ItemUnassignedRegulatorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves all the unassigned items(tasks) with status {@link RequestStatus#IN_PROGRESS}
     * The returned {@link ItemPage} contains a {@link List}  that contains all retrieved items filtered by the
     * request page and page size.
     *
     * @param scopedRequestTaskTypes {@link Map} that contains the set of accessible request task types per competent authority.
     * @param page the requested page
     * @param pageSize the requested page size
     * @return {@link ItemPage}
     */
    public ItemPage findUnassignedItems(String userId, Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes, Long page, Long pageSize) {
        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;
        QRequestTaskVisit requestTaskVisit = QRequestTaskVisit.requestTaskVisit;

        JPAQuery<Item> query = new JPAQuery<>(entityManager);

        BooleanBuilder hasCaPermissionOnTask = new BooleanBuilder();
        scopedRequestTaskTypes.forEach((ca, types) ->
                hasCaPermissionOnTask.or(request.competentAuthority.eq(ca).and(requestTask.type.in(types)))
        );

        JPAQuery<Item> jpaQuery = query.select(
                Projections.constructor(Item.class,
                        requestTask.startDate,
                        request.id, request.type, request.accountId,
                        requestTask.id, requestTask.type, requestTask.assignee,
                        requestTask.dueDate, requestTask.pauseDate, requestTaskVisit.isNull()))
                .from(request)
                .innerJoin(requestTask)
                .on(request.id.eq(requestTask.request.id))
                .leftJoin(requestTaskVisit)
                .on(requestTask.id.eq(requestTaskVisit.taskId).and(requestTaskVisit.userId.eq(userId)))
                .where(requestTask.assignee.isNull().and(hasCaPermissionOnTask))
                .orderBy(requestTask.startDate.desc())
                .offset(page * pageSize)
                .limit(pageSize);

        return ItemPage.builder()
                .items(jpaQuery.fetch())
                .totalItems(jpaQuery.fetchCount())
                .build();
    }

    /**
     * Retrieves all the unassigned items(tasks) with status {@link RequestStatus#IN_PROGRESS} for specific account.
     * The returned {@link ItemPage} contains a {@link List}  that contains all retrieved items filtered by the
     * request page and page size.
     *
     * @param userId User id
     * @param scopedRequestTaskTypes {@link Map} that contains the set of accessible request task types per competent authority.
     * @param accountId Account id
     * @param page the requested page
     * @param pageSize the requested page size
     * @return {@link ItemPage}
     */
    public ItemPage findUnassignedItemsByAccount(String userId, Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes,
                                                 Long accountId, Long page, Long pageSize) {
        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;
        QRequestTaskVisit requestTaskVisit = QRequestTaskVisit.requestTaskVisit;

        JPAQuery<Item> query = new JPAQuery<>(entityManager);

        BooleanBuilder hasCaPermissionOnTask = new BooleanBuilder();
        scopedRequestTaskTypes.forEach((ca, types) ->
                hasCaPermissionOnTask.or(request.competentAuthority.eq(ca).and(requestTask.type.in(types)))
        );

        JPAQuery<Item> jpaQuery = query.select(
                Projections.constructor(Item.class,
                        requestTask.startDate,
                        request.id, request.type, request.accountId,
                        requestTask.id, requestTask.type, requestTask.assignee,
                        requestTask.dueDate, requestTask.pauseDate, requestTaskVisit.isNull()))
                .from(request)
                .innerJoin(requestTask)
                .on(request.id.eq(requestTask.request.id))
                .leftJoin(requestTaskVisit)
                .on(requestTask.id.eq(requestTaskVisit.taskId).and(requestTaskVisit.userId.eq(userId)))
                .where(request.accountId.eq(accountId)
                        .and(request.status.eq(RequestStatus.IN_PROGRESS))
                        .and(requestTask.assignee.isNull())
                        .and(hasCaPermissionOnTask)
                ).orderBy(requestTask.startDate.desc())
                .offset(page * pageSize)
                .limit(pageSize);

        return ItemPage.builder()
                .items(jpaQuery.fetch())
                .totalItems(jpaQuery.fetchCount())
                .build();
    }
}
