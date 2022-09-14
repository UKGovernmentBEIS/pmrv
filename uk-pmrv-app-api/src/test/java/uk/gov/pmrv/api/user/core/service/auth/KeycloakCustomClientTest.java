package uk.gov.pmrv.api.user.core.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakRestEndPointEnum;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.core.SignatureRequest;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakSignature;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetails;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetailsRequest;

@ExtendWith(MockitoExtension.class)
@Import(ObjectMapper.class)
public class KeycloakCustomClientTest {
    
    @InjectMocks
    private KeycloakCustomClient client;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private KeycloakSpringBootProperties keycloakProperties;
    
    @Mock
    private KeycloakTokenProvider keycloakTokenProvider;
    
    private String authServerUrl = "http://serverurl";
    private String realm = "realm";
    
    @BeforeEach
    void init() {
        when(keycloakProperties.getAuthServerUrl()).thenReturn(authServerUrl);
        when(keycloakProperties.getRealm()).thenReturn(realm);
    }
    
    @Test
    void getUserDetails() {
        String userId = "userId";
        String token = "token";
        
        when(keycloakTokenProvider.grantToken()).thenReturn(token);
        
        UriComponents restPoint = UriComponentsBuilder
                .fromHttpUrl(authServerUrl + "/realms/" + realm + KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS.getEndPoint())
                .queryParam("userId", userId)
                .build();
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        
        String signatureUUID = UUID.randomUUID().toString();
        KeycloakUserDetails keycloakUserDetails = KeycloakUserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder().name("signature").uuid(signatureUUID).build())
                .build();
        
        when(restTemplate.exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakUserDetails>() {}, new HashMap<>()))
            .thenReturn(new ResponseEntity<KeycloakUserDetails>(keycloakUserDetails, HttpStatus.OK));
        
        Optional<UserDetails> result = client.getUserDetails(userId);
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(UserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder()
                        .name("signature").uuid(signatureUUID)
                        .build()).build());
        
        verify(restTemplate, times(1)).exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakUserDetails>() {}, new HashMap<>());
    }
    
    @Test
    void getUserDetails_when_request_throws_exception() {
        String userId = "userId";
        String token = "token";
        
        when(keycloakTokenProvider.grantToken()).thenReturn(token);
        
        UriComponents restPoint = UriComponentsBuilder
                .fromHttpUrl(authServerUrl + "/realms/" + realm + KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_DETAILS.getEndPoint())
                .queryParam("userId", userId)
                .build();
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        
        when(restTemplate.exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakUserDetails>() {}, new HashMap<>()))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        
        try{
            client.getUserDetails(userId);
            fail("Should not reach here");
        }catch (RuntimeException e) {
            assertThat(e.getCause().getClass()).isEqualTo(HttpClientErrorException.class);
        }catch (Exception e) {
            fail("Should not reach here");
        }
        
        verify(restTemplate, times(1)).exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakUserDetails>() {}, new HashMap<>());
    }
    
    @Test
    void saveUserDetails() {
        String userId = "userId";
        SignatureRequest signature = SignatureRequest.builder().content("content".getBytes()).name("name").size(3L).type("type").build();
        UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();
        
        String token = "token";
        when(keycloakTokenProvider.grantToken()).thenReturn(token);
        
        String restPoint = authServerUrl + "/realms/" + realm + KeycloakRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS.getEndPoint();
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        
        KeycloakUserDetailsRequest keycloakUserDetailsRequest = KeycloakUserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();
        
        when(restTemplate.exchange(restPoint, HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders), 
                new ParameterizedTypeReference<Void>() {}, new HashMap<>()))
            .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        
        client.saveUserDetails(userDetailsRequest);
        
        verify(restTemplate, times(1)).exchange(restPoint, HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders),
                new ParameterizedTypeReference<Void>() {}, new HashMap<>());
    }
    
    @Test
    void saveUserDetails_when_request_throws_exception() {
        String userId = "userId";
        SignatureRequest signature = SignatureRequest.builder().content("content".getBytes()).name("name").size(3L).type("type").build();
        UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();
        
        String token = "token";
        when(keycloakTokenProvider.grantToken()).thenReturn(token);
        
        String restPoint = authServerUrl + "/realms/" + realm + KeycloakRestEndPointEnum.KEYCLOAK_POST_USER_DETAILS.getEndPoint();
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        
        KeycloakUserDetailsRequest keycloakUserDetailsRequest = KeycloakUserDetailsRequest.builder()
                .id(userId)
                .signature(signature)
                .build();
        
        when(restTemplate.exchange(restPoint, HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders), 
                new ParameterizedTypeReference<Void>() {}, new HashMap<>()))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        
        try{
            client.saveUserDetails(userDetailsRequest);
            fail("Should not reach here");
        }catch (RuntimeException e) {
            assertThat(e.getCause().getClass()).isEqualTo(HttpClientErrorException.class);
        }catch (Exception e) {
            fail("Should not reach here");
        }
        
        verify(restTemplate, times(1)).exchange(restPoint, HttpMethod.POST, new HttpEntity<>(keycloakUserDetailsRequest, httpHeaders),
                new ParameterizedTypeReference<Void>() {}, new HashMap<>());
    }
    
    @Test
    void getUserSignature() {
        String signatureUuid = UUID.randomUUID().toString();
        String token = "token";
        
        when(keycloakTokenProvider.grantToken()).thenReturn(token);
        
        UriComponents restPoint = UriComponentsBuilder
                .fromHttpUrl(authServerUrl + "/realms/" + realm + KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE.getEndPoint())
                .queryParam("signatureUuid", signatureUuid)
                .build();
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        
        KeycloakSignature keycloakSignature = KeycloakSignature.builder()
                .name("name").content("content".getBytes()).size(1L).type("type")
                .build();
        
        FileDTO fileDTO = FileDTO.builder()
                .fileName("name").fileContent("content".getBytes()).fileSize(1L).fileType("type")
                .build();
        
        when(restTemplate.exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakSignature>() {}, new HashMap<>()))
            .thenReturn(new ResponseEntity<KeycloakSignature>(keycloakSignature, HttpStatus.OK));
        
        Optional<FileDTO> result = client.getUserSignature(signatureUuid);
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(fileDTO);
        
        verify(restTemplate, times(1)).exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakSignature>() {}, new HashMap<>());
    }
    
    @Test
    void getUserSignature_when_request_throws_exception() {
        String signatureUuid = UUID.randomUUID().toString();
        String token = "token";
        
        when(keycloakTokenProvider.grantToken()).thenReturn(token);
        
        UriComponents restPoint = UriComponentsBuilder
                .fromHttpUrl(authServerUrl + "/realms/" + realm + KeycloakRestEndPointEnum.KEYCLOAK_GET_USER_SIGNATURE.getEndPoint())
                .queryParam("signatureUuid", signatureUuid)
                .build();
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        
        when(restTemplate.exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakSignature>() {}, new HashMap<>()))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        
        try{
            client.getUserSignature(signatureUuid);
            fail("Should not reach here");
        }catch (RuntimeException e) {
            assertThat(e.getCause().getClass()).isEqualTo(HttpClientErrorException.class);
        }catch (Exception e) {
            fail("Should not reach here");
        }
        
        verify(restTemplate, times(1)).exchange(restPoint.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), 
                new ParameterizedTypeReference<KeycloakSignature>() {}, new HashMap<>());
    }

}
