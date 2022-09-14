package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

class RequestTaskTypeTest {

    @Test
    void getPeerReviewTypes() {
        Set<RequestTaskType> expectedRequestTaskTypes = Set.of(
            RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW
        );

        assertEquals(expectedRequestTaskTypes, RequestTaskType.getPeerReviewTypes());
    }
}