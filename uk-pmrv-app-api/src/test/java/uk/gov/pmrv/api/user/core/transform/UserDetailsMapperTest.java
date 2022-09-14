package uk.gov.pmrv.api.user.core.transform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;

public class UserDetailsMapperTest {

    private UserDetailsMapper userDetailsMapper = Mappers.getMapper(UserDetailsMapper.class);

    @Test
    void toUserDetails() {
        String userId = "userId";
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();
        
        UserDetailsRequest userDetailsRequest = userDetailsMapper.toUserDetails(userId, signature);
        
        assertThat(userDetailsRequest.getId()).isEqualTo(userId);
        assertThat(userDetailsRequest.getSignature().getContent()).isEqualTo(signature.getFileContent());
        assertThat(userDetailsRequest.getSignature().getName()).isEqualTo(signature.getFileName());
        assertThat(userDetailsRequest.getSignature().getSize()).isEqualTo(signature.getFileSize());
        assertThat(userDetailsRequest.getSignature().getType()).isEqualTo(signature.getFileType());
    }
}
