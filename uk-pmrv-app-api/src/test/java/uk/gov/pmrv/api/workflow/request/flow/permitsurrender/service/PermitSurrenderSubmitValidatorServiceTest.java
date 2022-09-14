package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderContainer;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderSubmitValidatorServiceTest {

    @InjectMocks
    private PermitSurrenderSubmitValidatorService service;
    
    @Mock
    private PermitSurrenderAttachmentsValidator permitSurrenderAttachmentsValidator;
    
    @Test
    void validatePermitSurrender() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        
        PermitSurrenderContainer permitSurrenderContainer = PermitSurrenderContainer.builder()
                .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justifiy").documentsExist(Boolean.TRUE).documents(Set.of(attachment1)).build())
                .permitSurrenderAttachments(Map.of(attachment1, "atta name1", attachment2, "atta name2"))
                .build();
        
        when(permitSurrenderAttachmentsValidator.attachmentsExist(Set.of(attachment1))).thenReturn(true);
        when(permitSurrenderAttachmentsValidator.sectionAttachmentsReferencedInPermitSurrender(Set.of(attachment1), Set.of(attachment1, attachment2))).thenReturn(true);
        
        service.validatePermitSurrender(permitSurrenderContainer);
        
        verify(permitSurrenderAttachmentsValidator, times(1)).attachmentsExist(Set.of(attachment1));
        verify(permitSurrenderAttachmentsValidator, times(1)).sectionAttachmentsReferencedInPermitSurrender(Set.of(attachment1), Set.of(attachment1, attachment2));
    }
    
    @Test
    void validatePermitSurrender_attachment_not_found_exception() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        
        PermitSurrenderContainer permitSurrenderContainer = PermitSurrenderContainer.builder()
                .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justifiy").documentsExist(Boolean.TRUE).documents(Set.of(attachment1)).build())
                .permitSurrenderAttachments(Map.of(attachment1, "atta name1", attachment2, "atta name2"))
                .build();
        
        when(permitSurrenderAttachmentsValidator.attachmentsExist(Set.of(attachment1))).thenReturn(false);
        
        BusinessException be = assertThrows(BusinessException.class, () -> service.validatePermitSurrender(permitSurrenderContainer));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMIT_SURRENDER);
        assertThat(be.getData()).isEqualTo(new Object[] {"Attachment not found"});
        
        verify(permitSurrenderAttachmentsValidator, times(1)).attachmentsExist(Set.of(attachment1));
        verifyNoMoreInteractions(permitSurrenderAttachmentsValidator); 
    }
    
    @Test
    void validatePermitSurrender_attachment_not_referenced_exception() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        
        PermitSurrenderContainer permitSurrenderContainer = PermitSurrenderContainer.builder()
                .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justifiy").documentsExist(Boolean.TRUE).documents(Set.of(attachment1)).build())
                .permitSurrenderAttachments(Map.of(attachment1, "atta name1", attachment2, "atta name2"))
                .build();
        
        when(permitSurrenderAttachmentsValidator.attachmentsExist(Set.of(attachment1))).thenReturn(true);
        when(permitSurrenderAttachmentsValidator.sectionAttachmentsReferencedInPermitSurrender(Set.of(attachment1), Set.of(attachment1, attachment2))).thenReturn(false);
        
        BusinessException be = assertThrows(BusinessException.class, () -> service.validatePermitSurrender(permitSurrenderContainer));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMIT_SURRENDER);
        assertThat(be.getData()).isEqualTo(new Object[] {"Attachment is not referenced in permit surrender"});
        
        verify(permitSurrenderAttachmentsValidator, times(1)).attachmentsExist(Set.of(attachment1));
        verify(permitSurrenderAttachmentsValidator, times(1)).sectionAttachmentsReferencedInPermitSurrender(Set.of(attachment1), Set.of(attachment1, attachment2)); 
    }
}
