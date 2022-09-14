package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactsDTO;
import uk.gov.pmrv.api.account.repository.CaExternalContactRepository;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CaExternalContactServiceTest {

    @InjectMocks
    private CaExternalContactService caExternalContactService;

    @Mock
    private CaExternalContactValidator caExternalContactValidator;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Mock
    private CaExternalContactRepository caExternalContactRepository;

    @Test
    void getCaExternalContacts_editable_false() {
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build()))
            .build();

        List<CaExternalContact> caExternalContacts = List.of(
            CaExternalContact.builder().id(1L).name("c1").build(),
            CaExternalContact.builder().id(2L).name("c2").build());

        List<CaExternalContactDTO> expectedCaExternalContactsDTOs = List.of(
                CaExternalContactDTO.builder().id(1L).name("c1").build(),
                CaExternalContactDTO.builder().id(2L).name("c2").build());

        when(caExternalContactRepository.findByCompetentAuthority(ca)).thenReturn(caExternalContacts);
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER))
            .thenReturn(false);

        CaExternalContactsDTO result = caExternalContactService.getCaExternalContacts(authUser);

        assertThat(result.getCaExternalContacts()).hasSameElementsAs(expectedCaExternalContactsDTOs);
        assertThat(result.isEditable()).isFalse();
    }

    @Test
    void getCaExternalContacts_editable_true() {
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca).build()))
            .build();

        List<CaExternalContact> caExternalContacts = List.of(
            CaExternalContact.builder().id(1L).name("c1").build(),
            CaExternalContact.builder().id(2L).name("c2").build());

        List<CaExternalContactDTO> expectedCaExternalContactsDTOs = List.of(
            CaExternalContactDTO.builder().id(1L).name("c1").build(),
            CaExternalContactDTO.builder().id(2L).name("c2").build());

        when(caExternalContactRepository.findByCompetentAuthority(ca))
            .thenReturn(caExternalContacts);
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER))
            .thenReturn(true);

        CaExternalContactsDTO result = caExternalContactService.getCaExternalContacts(authUser);

        assertThat(result.getCaExternalContacts()).hasSameElementsAs(expectedCaExternalContactsDTOs);
        assertThat(result.isEditable()).isTrue();
    }

    @Test
    void getCaExternalContactById() {
        Long id = 1L;
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca).build()))
            .build();

        CaExternalContact caExternalContact = CaExternalContact.builder()
            .id(1L)
            .name("c")
            .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca))
            .thenReturn(Optional.of(caExternalContact));

        CaExternalContactDTO caExternalContactDTO = CaExternalContactDTO.builder()
            .id(1L)
            .name("c")
            .build();

        assertThat(caExternalContactService.getCaExternalContactById(authUser, id)).isEqualTo(caExternalContactDTO);
    }

    @Test
    void getCaExternalContactById_contact_not_related_to_ca() {
        Long id = 1L;
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
            () -> caExternalContactService.getCaExternalContactById(authUser, id));

        assertEquals(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA, exception.getErrorCode());
    }
    
    @Test
    void getCaExternalContactEmailsByIds() {
    	Set<Long> ids = Set.of(1L, 2L);
    	List<CaExternalContact> contacts = List.of(
                CaExternalContact.builder().id(1L).name("c1").email("email1@email").build(),
                CaExternalContact.builder().id(2L).name("c2").email("email2@email").build());
    	
    	when(caExternalContactRepository.findAllByIdIn(ids)).thenReturn(contacts);
    	
    	List<String> result = caExternalContactService.getCaExternalContactEmailsByIds(ids);
    	
    	verify(caExternalContactRepository, times(1)).findAllByIdIn(ids);
    	assertThat(result).containsExactlyInAnyOrder("email1@email", "email2@email");
    }
    
    @Test
    void getCaExternalContactEmailsByIds_missing_id() {
    	Set<Long> ids = Set.of(1L, 3L);
    	List<CaExternalContact> contacts = List.of(
                CaExternalContact.builder().id(1L).name("c1").email("email1@email").build());
    	
    	when(caExternalContactRepository.findAllByIdIn(ids)).thenReturn(contacts);
    	
    	BusinessException exception = assertThrows(BusinessException.class,
                () -> caExternalContactService.getCaExternalContactEmailsByIds(ids));

        assertEquals(ErrorCode.EXTERNAL_CONTACT_CA_MISSING, exception.getErrorCode());
    }

    @Test
    void deleteCaExternalContactById() {
        Long id = 1L;
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();
        final CaExternalContact caExternalContact = CaExternalContact.builder()
            .id(id)
            .competentAuthority(ca)
            .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.of(caExternalContact));

        caExternalContactService.deleteCaExternalContactById(authUser, id);

        verify(caExternalContactRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteCaExternalContactById_contact_not_related_to_ca() {
        Long id = 1L;
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
            () -> caExternalContactService.deleteCaExternalContactById(authUser, id));

        assertEquals(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA, exception.getErrorCode());

        verify(caExternalContactRepository, never()).deleteById(anyLong());
    }

    @Test
    void createCaExternalContact() {

        final CompetentAuthority ca = ENGLAND;
        final String name = "name";
        final String email = "email";
        final String description = "description";

        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .description(description)
                .build();

        caExternalContactService.createCaExternalContact(authUser, caExternalContactRegistrationDTO);

        ArgumentCaptor<CaExternalContact> caExternalContactArgumentCaptor = ArgumentCaptor.forClass(CaExternalContact.class);
        verify(caExternalContactRepository, times(1)).save(caExternalContactArgumentCaptor.capture());
        CaExternalContact caExternalContact = caExternalContactArgumentCaptor.getValue();
        assertThat(caExternalContact.getName()).isEqualTo(name);
        assertThat(caExternalContact.getEmail()).isEqualTo(email);
        assertThat(caExternalContact.getDescription()).isEqualTo(description);
        assertThat(caExternalContact.getCompetentAuthority()).isEqualTo(ca);
    }

    @Test
    void createCaExternalContact_ca_name_already_exists() {

        final CompetentAuthority ca = ENGLAND;
        final String name = "name";
        final String email = "email";
        final String description = "description";

        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .description(description)
                .build();

        doThrow(new BusinessException((ErrorCode.EXTERNAL_CONTACT_CA_NAME_ALREADY_EXISTS))).when(caExternalContactValidator)
            .validateCaExternalContactRegistration(ca, caExternalContactRegistrationDTO);

        BusinessException businessException =
            assertThrows(BusinessException.class,
                () ->  caExternalContactService.createCaExternalContact(authUser, caExternalContactRegistrationDTO));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_ALREADY_EXISTS);
        verify(caExternalContactRepository, never()).save(any());
    }

    @Test
    void createCaExternalContact_ca_email_already_exists() {

        final CompetentAuthority ca = ENGLAND;
        final String name = "name";
        final String email = "email";
        final String description = "description";

        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .description(description)
                .build();

        doThrow(new BusinessException((ErrorCode.EXTERNAL_CONTACT_CA_EMAIL_ALREADY_EXISTS))).when(caExternalContactValidator)
            .validateCaExternalContactRegistration(ca, caExternalContactRegistrationDTO);


        BusinessException businessException =
            assertThrows(BusinessException.class,
                () ->  caExternalContactService.createCaExternalContact(authUser, caExternalContactRegistrationDTO));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_EMAIL_ALREADY_EXISTS);
        verify(caExternalContactRepository, never()).save(any());
    }
    @Test
    void createCaExternalContact_ca_name_email_already_exists() {

        final CompetentAuthority ca = ENGLAND;
        final String name = "name";
        final String email = "email";
        final String description = "description";

        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .description(description)
                .build();

        doThrow(new BusinessException((ErrorCode.EXTERNAL_CONTACT_CA_NAME_EMAIL_ALREADY_EXISTS))).when(caExternalContactValidator)
            .validateCaExternalContactRegistration(ca, caExternalContactRegistrationDTO);


        BusinessException businessException =
            assertThrows(BusinessException.class,
                () ->  caExternalContactService.createCaExternalContact(authUser, caExternalContactRegistrationDTO));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_EMAIL_ALREADY_EXISTS);
        verify(caExternalContactRepository, never()).save(any());
    }


    @Test
    void editCaExternalContact() {

        final Long id = 1L;
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                   .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email")
                .description("description")
                .build();

        CaExternalContact caExternalContact =
            CaExternalContact.builder()
                .id(id)
                .competentAuthority(ca)
                .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.of(caExternalContact));

        caExternalContactService.editCaExternalContact(authUser, id, caExternalContactRegistrationDTO);

        ArgumentCaptor<CaExternalContact> caExternalContactArgumentCaptor = ArgumentCaptor.forClass(CaExternalContact.class);
        verify(caExternalContactRepository, times(1)).save(caExternalContactArgumentCaptor.capture());
        CaExternalContact caExternalContactCaptured = caExternalContactArgumentCaptor.getValue();
        assertThat(caExternalContactCaptured.getName()).isEqualTo(caExternalContactRegistrationDTO.getName());
        assertThat(caExternalContactCaptured.getEmail()).isEqualTo(caExternalContactRegistrationDTO.getEmail());
        assertThat(caExternalContactCaptured.getDescription()).isEqualTo(caExternalContactRegistrationDTO.getDescription());
        assertThat(caExternalContactCaptured.getCompetentAuthority()).isEqualTo(ca);
    }

    @Test
    void editCaExternalContact_ca_email_contact_not_related_to_ca() {

        final Long id = 1L;
        final CompetentAuthority ca = ENGLAND;

        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email")
                .description("description")
                .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.empty());

        BusinessException businessException =
            assertThrows(BusinessException.class,
                () ->  caExternalContactService.editCaExternalContact(authUser, id, caExternalContactRegistrationDTO));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA);
        verify(caExternalContactRepository, never()).existsByCompetentAuthorityAndNameAndIdNot(any(), anyString(), anyLong());
        verify(caExternalContactRepository, never()).existsByCompetentAuthorityAndEmailAndIdNot(any(), anyString(), anyLong());
        verify(caExternalContactRepository, never()).save(any());
    }

    @Test
    void editCaExternalContact_ca_name_already_exists() {

        final Long id = 1L;
        final CompetentAuthority ca = ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email")
                .description("description")
                .build();

        CaExternalContact caExternalContact =
            CaExternalContact.builder()
                .id(id)
                .competentAuthority(ca)
                .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.of(caExternalContact));
        doThrow(new BusinessException((ErrorCode.EXTERNAL_CONTACT_CA_NAME_ALREADY_EXISTS))).when(caExternalContactValidator)
            .validateCaExternalContactRegistration(ca, id, caExternalContactRegistrationDTO);

        BusinessException businessException =
            assertThrows(BusinessException.class,
                () ->  caExternalContactService.editCaExternalContact(authUser, id, caExternalContactRegistrationDTO));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_ALREADY_EXISTS);
        verify(caExternalContactRepository, never()).existsByCompetentAuthorityAndEmailAndIdNot(ca, caExternalContactRegistrationDTO.getEmail(), id);
        verify(caExternalContactRepository, never()).save(any());
    }

    @Test
    void editCaExternalContact_ca_email_already_exists() {

        final Long id = 1L;
        final CompetentAuthority ca = ENGLAND;

        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email")
                .description("description")
                .build();

        CaExternalContact caExternalContact =
            CaExternalContact.builder()
                .id(id)
                .competentAuthority(ca)
                .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.of(caExternalContact));
        doThrow(new BusinessException((ErrorCode.EXTERNAL_CONTACT_CA_EMAIL_ALREADY_EXISTS))).when(caExternalContactValidator)
            .validateCaExternalContactRegistration(ca, id, caExternalContactRegistrationDTO);

        BusinessException businessException =
            assertThrows(BusinessException.class,
                () ->  caExternalContactService.editCaExternalContact(authUser, id, caExternalContactRegistrationDTO));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_EMAIL_ALREADY_EXISTS);
        verify(caExternalContactRepository, never()).save(any());
    }

    @Test
    void editCaExternalContact_ca_name_email_already_exists() {

        final Long id = 1L;
        final CompetentAuthority ca = ENGLAND;

        PmrvUser authUser = PmrvUser.builder()
            .roleType(REGULATOR)
            .authorities(List.of(
                PmrvAuthority
                    .builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        CaExternalContactRegistrationDTO caExternalContactRegistrationDTO =
            CaExternalContactRegistrationDTO.builder()
                .name("name")
                .email("email")
                .description("description")
                .build();

        CaExternalContact caExternalContact =
            CaExternalContact.builder()
                .id(id)
                .competentAuthority(ca)
                .build();

        when(caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)).thenReturn(Optional.of(caExternalContact));
        doThrow(new BusinessException((ErrorCode.EXTERNAL_CONTACT_CA_NAME_EMAIL_ALREADY_EXISTS))).when(caExternalContactValidator)
            .validateCaExternalContactRegistration(ca, id, caExternalContactRegistrationDTO);

        BusinessException businessException =
            assertThrows(BusinessException.class,
                () ->  caExternalContactService.editCaExternalContact(authUser, id, caExternalContactRegistrationDTO));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_EMAIL_ALREADY_EXISTS);
        verify(caExternalContactRepository, never()).save(any());
    }
}
