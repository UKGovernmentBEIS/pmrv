package uk.gov.pmrv.api.web.controller.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.dto.validation.CountryValidator;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
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
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestCreateActionProcessDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountOpeningSubmitApplicationCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandlerMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private static final String BASE_PATH = "/v1.0/requests";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestController requestController;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private RequestCreateActionHandlerMapper requestCreateActionHandlerMapper;
    
    @Mock
    private InstallationAccountOpeningSubmitApplicationCreateActionHandler handler;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RequestQueryService requestQueryService;
    
    @Mock
    private RequestService requestService;

    private ObjectMapper mapper;
    
    @Mock
    private CountryService countryService;
    
    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(requestController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        requestController = (RequestController) aopProxy.getProxy();
        
        LocalValidatorFactoryBean validatorFactoryBean = mockValidatorFactoryBean();
        
        mockMvc = MockMvcBuilders.standaloneSetup(requestController)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setValidator(validatorFactoryBean)
                .build();
    }

    @Test
    void processRequestCreateAction() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().userId("id").build();
        InstallationAccountOpeningSubmitApplicationCreateActionPayload payload = InstallationAccountOpeningSubmitApplicationCreateActionPayload.builder()
            .payloadType(RequestCreateActionPayloadType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD)
            .accountPayload(AccountPayload.builder()
                    .accountType(AccountType.INSTALLATION)
                    .name("name")
                    .siteName("site name")
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                    .commencementDate(LocalDate.of(2020,8,6))
                    .location(LocationOnShoreDTO.builder()
                            .type(LocationType.ONSHORE)
                            .gridReference("NN166718")
                            .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build()).build())
                    .legalEntity(LegalEntityDTO.builder()
                        .type(LegalEntityType.PARTNERSHIP)
                        .name("name")
                        .referenceNumber("09546038")
                        .noReferenceNumberReason("noCompaniesRefDetails")
                        .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build())
                        .build())
                    .build())
            .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestCreateActionType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION)
                .requestCreateActionPayload(payload)
                .build();

        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(requestCreateActionHandlerMapper.get(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION)).thenReturn(handler);

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestCreateActionHandlerMapper, times(1)).get(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION);
        verify(handler, times(1)).process(null, RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION, payload, pmrvUser);
        verify(countryService, times(2)).getReferenceData();
    }
    
    @Test
    void processRequestCreateAction_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().userId("id").build();
        InstallationAccountOpeningSubmitApplicationCreateActionPayload payload = InstallationAccountOpeningSubmitApplicationCreateActionPayload.builder()
            .payloadType(RequestCreateActionPayloadType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD)
            .accountPayload(AccountPayload.builder()
                    .accountType(AccountType.INSTALLATION)
                    .name("name")
                    .siteName("site name")
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                    .commencementDate(LocalDate.of(2020,8,6))
                    .location(LocationOnShoreDTO.builder()
                            .type(LocationType.ONSHORE)
                            .gridReference("NN166718")
                            .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build()).build())
                    .legalEntity(LegalEntityDTO.builder()
                        .type(LegalEntityType.PARTNERSHIP)
                        .name("name")
                        .referenceNumber("09546038")
                        .noReferenceNumberReason("noCompaniesRefDetails")
                        .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build())
                        .build())
                    .build())
            .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestCreateActionType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION)
                .requestCreateActionPayload(payload)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(pmrvUser, "processRequestCreateAction", null, RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(countryService, times(2)).getReferenceData();
        verifyNoInteractions(requestCreateActionHandlerMapper, handler);
    }

    @Test
    void getRequestDetailsById() throws Exception {
        final String requestId = "1";
        AerRequestMetadata metadata = AerRequestMetadata.builder()
                .type(RequestMetadataType.AER).emissions(BigDecimal.valueOf(10000)).build();
        RequestDetailsDTO workflowInfo = new RequestDetailsDTO(requestId, RequestType.AER,
                RequestStatus.IN_PROGRESS, LocalDateTime.now(), metadata);

        when(requestQueryService.findRequestDetailsById(requestId)).thenReturn(workflowInfo);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workflowInfo.getId()))
                .andExpect(jsonPath("$.requestType").value(workflowInfo.getRequestType().name()))
                .andExpect(jsonPath("$.requestStatus").value(workflowInfo.getRequestStatus().name()))
                .andExpect(jsonPath("$.requestMetadata.type").value(metadata.getType().name()))
                .andExpect(jsonPath("$.requestMetadata.emissions").value(metadata.getEmissions()));

        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
    }

    @Test
    void getRequestDetailsById_forbidden() throws Exception {
        final String requestId = "1";
        PmrvUser pmrvUser = PmrvUser.builder().userId("id").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(pmrvUser, "getRequestDetailsById", requestId);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsById(anyString());
    }

    @Test
    void getRequestDetailsByAccountId() throws Exception {
        Long accountId = 1L;
        final String requestId = "1";
        RequestSearchCriteria criteria = RequestSearchCriteria.builder().accountId(accountId).page(0L)
                .pageSize(30L).category(RequestCategory.PERMIT).build();

        RequestDetailsDTO workflowResult1 = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);
        RequestDetailsDTO workflowResult2 = new RequestDetailsDTO(requestId, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        RequestDetailsSearchResults results = RequestDetailsSearchResults.builder()
                .requestDetails(List.of(workflowResult1, workflowResult2))
                .total(10L)
                .build();

        when(requestQueryService.findRequestDetailsBySearchCriteria(criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                .content(mapper.writeValueAsString(criteria))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.requestDetails[0].id").value(workflowResult1.getId()))
                .andExpect(jsonPath("$.requestDetails[1].id").value(workflowResult2.getId()))
        ;

        verify(requestQueryService, times(1)).findRequestDetailsBySearchCriteria(criteria);
    }

    @Test
    void getRequestDetailsByAccountId_forbidden() throws Exception {
        Long accountId = 1L;
        PmrvUser user = PmrvUser.builder().userId("user").build();
        RequestSearchCriteria criteria = RequestSearchCriteria.builder().accountId(accountId).page(0L)
                .pageSize(30L).category(RequestCategory.PERMIT).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "getRequestDetailsByAccountId", String.valueOf(accountId));

        mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsBySearchCriteria(any());
    }

    @Test
    void getAvailableWorkflowsByAccountId() throws Exception {
        final RequestCategory category = RequestCategory.PERMIT;
        final Long accountId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("id").build();
        final Map<RequestCreateActionType, RequestCreateValidationResult> results =
            Map.of(RequestCreateActionType.PERMIT_SURRENDER,
                   RequestCreateValidationResult.builder().valid(true).build());

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(requestService.getAvailableWorkflows(accountId, pmrvUser, category)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/available-workflows/" + accountId)
                .param("category", category.name()))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"PERMIT_SURRENDER\":{\"valid\":true}}"));

        verify(requestService, times(1)).getAvailableWorkflows(accountId, pmrvUser, category);
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
}
