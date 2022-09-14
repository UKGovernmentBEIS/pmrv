package uk.gov.pmrv.api.workflow.request.flow.rde.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeSubmittedRequestActionPayload;

class RdeMapperTest {

    private RdeMapper mapper = Mappers.getMapper(RdeMapper.class);
    
    @Test
    void toRdeSubmittedRequestActionPayload() {
        RdePayload rdePayload = RdePayload.builder()
                .extensionDate(LocalDate.now().plusDays(10))
                .deadline(LocalDate.now().plusDays(1))
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        
        RdeSubmitRequestTaskActionPayload taskActionPayload = RdeSubmitRequestTaskActionPayload.builder()
                .rdePayload(rdePayload)
                .build();
        
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "user1", RequestActionUserInfo.builder().name("user1").build(),
                "user2", RequestActionUserInfo.builder().name("user2").build()
                );
        
        FileInfoDTO officialDocument = FileInfoDTO.builder()
                .name("off_doc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        
        RdeSubmittedRequestActionPayload result = mapper.toRdeSubmittedRequestActionPayload(taskActionPayload, usersInfo, officialDocument);
        
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.RDE_SUBMITTED_PAYLOAD);
        assertThat(result.getRdePayload()).isEqualTo(rdePayload);
        assertThat(result.getOfficialDocument()).isEqualTo(officialDocument);
        assertThat(result.getUsersInfo()).isEqualTo(usersInfo);
    }
}
