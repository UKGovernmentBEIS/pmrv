package uk.gov.pmrv.api.workflow.request.application.taskview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

class RequestInfoMapperTest {

    private RequestInfoMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RequestInfoMapper.class);
    }

    @Test
    void toRequestInfoDTO() {
        final String requestId = "1";
        RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        CompetentAuthority ca = CompetentAuthority.ENGLAND;

        Request request = Request.builder()
            .id(requestId)
            .type(requestType)
            .competentAuthority(ca)
            .status(RequestStatus.IN_PROGRESS)
            .accountId(1L)
            .build();

        RequestInfoDTO result = mapper.toRequestInfoDTO(request);

        assertEquals(requestId, result.getId());
        assertEquals(requestType, result.getType());
        assertEquals(ca, result.getCompetentAuthority());
        assertEquals(1L, result.getAccountId());
    }
}
