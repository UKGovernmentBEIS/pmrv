package uk.gov.pmrv.api.user.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
public class UserSignatureServiceTest {

    @InjectMocks
    private UserSignatureService service;

    @Mock
    private UserAuthService userAuthService;
    
    @Mock
    private UserFileTokenService userFileTokenService;
    
    @Test
    void generateSignatureFileToken() {
        String userId = "userId"; 
        UUID signatureUuid = UUID.randomUUID();
        UserDetails userDetails = UserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder().uuid(signatureUuid.toString()).name("sign").build())
                .build();
        FileToken token = FileToken.builder()
                .token("token")
                .tokenExpirationMinutes(1)
                .build();
        
        when(userAuthService.getUserDetails(userId)).thenReturn(Optional.of(userDetails));
        when(userFileTokenService.generateGetFileToken(signatureUuid.toString())).thenReturn(token);
        
        FileToken result = service.generateSignatureFileToken(userId, signatureUuid);
        assertThat(result).isEqualTo(token);
        verify(userAuthService, times(1)).getUserDetails(userId);
        verify(userFileTokenService, times(1)).generateGetFileToken(signatureUuid.toString());
    }
    
    @Test
    void generateSignatureFileToken_user_not_found() {
        String userId = "userId"; 
        UUID signatureUuid = UUID.randomUUID();
        
        when(userAuthService.getUserDetails(userId)).thenReturn(Optional.empty());
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> service.generateSignatureFileToken(userId, signatureUuid));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(userAuthService, times(1)).getUserDetails(userId);
        verifyNoInteractions(userFileTokenService);
    }
    
    @Test
    void generateSignatureFileToken_no_signature() {
        String userId = "userId"; 
        UUID signatureUuid = UUID.randomUUID();
        UserDetails userDetails = UserDetails.builder()
                .id(userId)
                .build();

        when(userAuthService.getUserDetails(userId)).thenReturn(Optional.of(userDetails));
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> service.generateSignatureFileToken(userId, signatureUuid));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(userAuthService, times(1)).getUserDetails(userId);
        verifyNoInteractions(userFileTokenService);
    }
    
    @Test
    void generateSignatureFileToken_signature_not_same() {
        String userId = "userId"; 
        UUID signatureUuid = UUID.randomUUID();
        UserDetails userDetails = UserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("sign").build())
                .build();
        
        when(userAuthService.getUserDetails(userId)).thenReturn(Optional.of(userDetails));
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> service.generateSignatureFileToken(userId, signatureUuid));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(userAuthService, times(1)).getUserDetails(userId);
        verifyNoInteractions(userFileTokenService);
    }
    
    @Test
    void getSignatureFileDTOByToken() {
        String getFileToken = "token"; 
        
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();
        
        when(userFileTokenService.resolveGetFileUuid(getFileToken)).thenReturn("fileUuid");
        when(userAuthService.getUserSignature("fileUuid")).thenReturn(Optional.of(signature));
        
        FileDTO result = service.getSignatureFileDTOByToken(getFileToken);
        assertThat(result).isEqualTo(signature);
        verify(userFileTokenService, times(1)).resolveGetFileUuid(getFileToken);
        verify(userAuthService, times(1)).getUserSignature("fileUuid");
    }
}
