package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PasswordClientService;
import uk.gov.pmrv.api.user.core.domain.enumeration.RestEndPointEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PasswordClientServiceTest {

    @InjectMocks
    private PasswordClientService passwordClientService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private RestTemplate restTemplate;

    private static final String baseUrl = "url";

    private static final String strongPasswordHashPrefix = "c7481";

    private static final String strongPasswordResponse = "1AA8423017483440CC271B810DEB524E139:2";

    @Test
    void searchPassword() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("text/plain")));

        Map<String, Object> requestParams = new HashMap<>(){{
            put(RestEndPointEnum.PWNED_PASSWORDS.getParameters().get(0), strongPasswordHashPrefix);
        }};

        when(appProperties.getClient()).thenReturn(mock(AppProperties.Client.class));
        when(appProperties.getClient().getPasswordUrl()).thenReturn(baseUrl);
        when(restTemplate.exchange(baseUrl + RestEndPointEnum.PWNED_PASSWORDS.getEndPoint(), RestEndPointEnum.PWNED_PASSWORDS.getMethod(),
                new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<String>() {}, requestParams))
                .thenReturn(new ResponseEntity<>(strongPasswordResponse, HttpStatus.OK));

        passwordClientService.searchPassword(strongPasswordHashPrefix);

        verify(appProperties.getClient(), times(1)).getPasswordUrl();
        verify(restTemplate, times(1)).exchange(baseUrl + RestEndPointEnum.PWNED_PASSWORDS.getEndPoint(),
                RestEndPointEnum.PWNED_PASSWORDS.getMethod(), new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<String>() {}, requestParams);
    }

    @Test
    void searchPasswordThrowsException() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("text/plain")));

        Map<String, Object> requestParams = new HashMap<>(){{
            put(RestEndPointEnum.PWNED_PASSWORDS.getParameters().get(0), strongPasswordHashPrefix);
        }};

        when(appProperties.getClient()).thenReturn(mock(AppProperties.Client.class));
        when(appProperties.getClient().getPasswordUrl()).thenReturn(baseUrl);
        when(restTemplate.exchange(baseUrl + RestEndPointEnum.PWNED_PASSWORDS.getEndPoint(), RestEndPointEnum.PWNED_PASSWORDS.getMethod(),
                new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<String>() {}, requestParams))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(Exception.class, () -> passwordClientService.searchPassword(strongPasswordHashPrefix));

        verify(appProperties.getClient(), times(1)).getPasswordUrl();
        verify(restTemplate, times(1)).exchange(baseUrl + RestEndPointEnum.PWNED_PASSWORDS.getEndPoint(),
                RestEndPointEnum.PWNED_PASSWORDS.getMethod(), new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<String>() {}, requestParams);
    }
}
