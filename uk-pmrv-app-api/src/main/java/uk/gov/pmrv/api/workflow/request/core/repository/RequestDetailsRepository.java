package uk.gov.pmrv.api.workflow.request.core.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Set;

@Repository
public class RequestDetailsRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    public RequestDetailsSearchResults findRequestDetailsBySearchCriteria(RequestSearchCriteria criteria) {
        QRequest request = QRequest.request;

        JPAQuery<RequestDetailsDTO> query = new JPAQuery<>(entityManager);

        Set<RequestType> requestTypes = ObjectUtils.isEmpty(criteria.getRequestTypes())
                ? RequestType.getRequestTypesByCategory(criteria.getCategory())
                : RequestType.getRequestTypesByCategory(criteria.getCategory(), criteria.getRequestTypes());

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause
            .and(request.accountId.eq(criteria.getAccountId()))
            .and(request.type.in(requestTypes));
        
        if(criteria.getStatus() != null) {
            switch (criteria.getStatus()) {
            case OPEN:
                whereClause.and(request.status.eq(RequestStatus.IN_PROGRESS));
                break;
            case CLOSED:
                whereClause.and(request.status.ne(RequestStatus.IN_PROGRESS));
                break;
            default:
                throw new UnsupportedOperationException(String.format("The workflow status: %s is not supported", criteria.getStatus()));
            }
        }
        
        JPAQuery<RequestDetailsDTO> jpaQuery = query.select(
                Projections.constructor(RequestDetailsDTO.class,
                        request.id, 
                        request.type, 
                        request.status,
                        request.creationDate,
                        request.metadata))
                .from(request)
                .where(whereClause)
                .orderBy(request.creationDate.desc())
                .offset(criteria.getPage() * criteria.getPageSize())
                .limit(criteria.getPageSize());

        return RequestDetailsSearchResults.builder()
                .requestDetails(jpaQuery.fetch())
                .total(jpaQuery.fetchCount())
                .build();
    }

    public RequestDetailsDTO findRequestDetailsById(String requestId) {
        QRequest request = QRequest.request;

        JPAQuery<RequestDetailsDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<RequestDetailsDTO> jpaQuery = query.select(
                Projections.constructor(RequestDetailsDTO.class,
                        request.id,
                        request.type,
                        request.status,
                        request.creationDate,
                        request.metadata))
                .from(request)
                .where(request.id.eq(requestId));

        return jpaQuery.fetchFirst();
    }
}
