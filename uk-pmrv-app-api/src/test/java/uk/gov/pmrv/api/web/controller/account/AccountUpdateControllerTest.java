package uk.gov.pmrv.api.web.controller.account;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.account.domain.dto.AccountUpdateLegalEntityNameDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountUpdateRegistryIdDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountUpdateSiteNameDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.domain.dto.AccountUpdateSopIdDTO;
import uk.gov.pmrv.api.account.service.AccountUpdateService;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.dto.validation.CountryValidator;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.service.CountryService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.controller.utils.TestConstrainValidatorFactory;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AccountUpdateControllerTest {

private static final String CONTROLLER_PATH = "/v1.0/accounts";
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private AccountUpdateController controller;
    
    @Mock
    private AccountUpdateService accountUpdateService;
    
    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;
    
    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private CountryService countryService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (AccountUpdateController) aopProxy.getProxy();

        LocalValidatorFactoryBean validatorFactoryBean = mockValidatorFactoryBean();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setValidator(validatorFactoryBean)
                .build();

        objectMapper = new ObjectMapper();
    }
    
    @Test
    void updateAccountSiteName() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSiteNameDTO siteNameDTO = AccountUpdateSiteNameDTO.builder().siteName("newSiteName").build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/site-name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(siteNameDTO)))
                    .andExpect(status().isNoContent());
        
        verify(accountUpdateService, times(1)).updateAccountSiteName(accountId, siteNameDTO.getSiteName());
    }
    
    @Test
    void updateAccountSiteName_forbidden() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSiteNameDTO siteNameDTO = AccountUpdateSiteNameDTO.builder().siteName("newSiteName").build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "updateAccountSiteName", String.valueOf(accountId));
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/site-name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(siteNameDTO)))
                    .andExpect(status().isForbidden());
        
        verify(accountUpdateService, never())
            .updateAccountSiteName(Mockito.anyLong(), Mockito.anyString());
    }
    
    @Test
    void updateAccountRegistryId() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AccountUpdateRegistryIdDTO registryIdDTO = AccountUpdateRegistryIdDTO.builder().registryId(1234567).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/registry-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registryIdDTO)))
                    .andExpect(status().isNoContent());
        
        verify(accountUpdateService, times(1)).updateAccountRegistryId(accountId, registryIdDTO.getRegistryId());
    }
    
    @Test
    void updateAccountRegistryId_forbidden() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AccountUpdateRegistryIdDTO registryIdDTO = AccountUpdateRegistryIdDTO.builder().registryId(1234567).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "updateAccountRegistryId", String.valueOf(accountId));
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/registry-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registryIdDTO)))
                    .andExpect(status().isForbidden());
        
        verify(accountUpdateService, never())
            .updateAccountRegistryId(Mockito.anyLong(), Mockito.anyInt());
    }
    
    @Test
    void updateAccountSopId() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSopIdDTO sopIdDTO = AccountUpdateSopIdDTO.builder().sopId(1234567899L).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/sop-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sopIdDTO)))
                    .andExpect(status().isNoContent());
        
        verify(accountUpdateService, times(1)).updateAccountSopId(accountId, sopIdDTO.getSopId());
    }
    
    @Test
    void updateAccountSopId_forbidden() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSopIdDTO sopIdDTO = AccountUpdateSopIdDTO.builder().sopId(1234567899L).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "updateAccountSopId", String.valueOf(accountId));
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/sop-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sopIdDTO)))
                    .andExpect(status().isForbidden());
        
        verify(accountUpdateService, never())
            .updateAccountRegistryId(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void updateAccountAddress() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        LocationOnShoreDTO address = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).gridReference("te12345").address(AddressDTO.builder()
                .city("city").country("GR").line1("line1").line2("line2").postcode("postcode").build()).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(CONTROLLER_PATH + "/" + accountId + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        verify(accountUpdateService, times(1)).updateAccountAddress(accountId, address);
        verify(countryService, times(1)).getReferenceData();
    }

    @Test
    void updateAccountAddress_forbidden() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        LocationOnShoreDTO address = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).gridReference("te12345").address(AddressDTO.builder()
                .city("city").country("GR").line1("line1").line2("line2").postcode("postcode").build()).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "updateAccountAddress", String.valueOf(accountId));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(CONTROLLER_PATH + "/" + accountId + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isForbidden());

        verify(accountUpdateService, never()).updateAccountAddress(Mockito.anyLong(), Mockito.any());
        verify(countryService, times(1)).getReferenceData();
    }

    private LocalValidatorFactoryBean mockValidatorFactoryBean() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        MockServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();

        beanFactory.registerSingleton(CountryValidator.class.getCanonicalName(), new CountryValidator(countryService));

        context.refresh();
        validatorFactoryBean.setApplicationContext(context);
        TestConstrainValidatorFactory constraintValidatorFactory = new TestConstrainValidatorFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constraintValidatorFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    @Test
    void updateAccountLegalEntityName() throws Exception {

        final PmrvUser user = PmrvUser.builder().build();
        final Long accountId = 1L;
        final AccountUpdateLegalEntityNameDTO name =
            AccountUpdateLegalEntityNameDTO.builder().legalEntityName("new name").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/legal-entity/name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(name)))
            .andExpect(status().isNoContent());

        verify(accountUpdateService, times(1)).updateLegalEntityName(accountId, name.getLegalEntityName());
    }

    @Test
    @DisplayName("Update Account Legal Entity Address should succeed")
    void updateAccountLegalEntityAddress() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AddressDTO address = AddressDTO.builder()
            .city("city")
            .country("GR")
            .line1("line1")
            .line2("line2")
            .postcode("postcode")
            .build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/legal-entity/address")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(address)))
            .andExpect(status().isNoContent());

        verify(accountUpdateService, times(1))
            .updateLegalEntityAddress(accountId, address);
        verify(countryService, times(1)).getReferenceData();
    }

    @Test
    @DisplayName("Update Account Legal Entity Address should fail for BusinessException")
    void updateAccountLegalEntityAddressForbidden() throws Exception {
        PmrvUser user = PmrvUser.builder().build();
        Long accountId = 1L;
        AddressDTO address = AddressDTO.builder()
            .city("city")
            .country("GR")
            .line1("line1")
            .line2("line2")
            .postcode("postcode")
            .build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "updateLegalEntityAddress", String.valueOf(accountId));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/legal-entity/address")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(address)))
            .andExpect(status().isForbidden());

        verify(accountUpdateService, never())
            .updateLegalEntityAddress(Mockito.anyLong(), Mockito.any());
        verify(countryService, times(1)).getReferenceData();
    }
}
