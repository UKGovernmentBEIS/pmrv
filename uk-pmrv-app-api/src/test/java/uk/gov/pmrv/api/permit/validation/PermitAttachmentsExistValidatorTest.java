package uk.gov.pmrv.api.permit.validation;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage;
import uk.gov.pmrv.api.permit.domain.sitediagram.SiteDiagrams;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitAttachmentsExistValidatorTest {

    @InjectMocks
    private PermitAttachmentsExistValidator validator;
    
    @Mock
    private PermitReferenceService permitReferenceService;
    
    @Test
    void validate() {
        UUID siteDiagramUuid = UUID.randomUUID();
        UUID anotherFileUuid = UUID.randomUUID();
        
        Map<UUID, String> permitAttachments = Map.of(anotherFileUuid, "another.pdf");
        
        PermitContainer permitContainer = PermitContainer.builder()
                .permit(Permit.builder()
                        .siteDiagrams(SiteDiagrams.builder().siteDiagrams(Set.of(siteDiagramUuid)).build())
                        .build())
                .permitAttachments(permitAttachments)
                .build();

        when(permitReferenceService.validateFilesExist(Set.of(siteDiagramUuid), permitAttachments.keySet()))
                .thenReturn(Optional.of(Pair.of(PermitViolationMessage.ATTACHMENT_NOT_FOUND, List.of())));
        
        final PermitValidationResult result = validator.validate(permitContainer);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getPermitViolations().get(0).getMessage()).isEqualTo(PermitViolationMessage.ATTACHMENT_NOT_FOUND.getMessage());
        verify(permitReferenceService, times(1)).validateFilesExist(Set.of(siteDiagramUuid), permitAttachments.keySet());
    }
    
}
