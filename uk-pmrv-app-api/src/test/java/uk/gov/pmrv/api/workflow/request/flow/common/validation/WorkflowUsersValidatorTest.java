package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactsDTO;
import uk.gov.pmrv.api.account.service.CaExternalContactService;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

@ExtendWith(MockitoExtension.class)
class WorkflowUsersValidatorTest {

    @InjectMocks
    private WorkflowUsersValidator validator;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Mock
    private CaExternalContactService caExternalContactService;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Test
    void validate_whenOperatorsNotValid_thenThrowException() {

        final PmrvUser pmrvUser = PmrvUser.builder()
            .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
            .build();

        final UserAuthorityDTO accountOperatorAuthority =
            UserAuthorityDTO.builder().userId("operator2").authorityStatus(ACTIVE).build();
        final UserAuthoritiesDTO accountOperatorAuthorities =
            UserAuthoritiesDTO.builder()
                .authorities(List.of(accountOperatorAuthority))
                .editable(true)
                .build();

        when(operatorAuthorityQueryService.getAccountAuthorities(pmrvUser, 1L)).thenReturn(accountOperatorAuthorities);

        final boolean result = validator.areOperatorsValid(1L, Set.of("operator1"), pmrvUser);

        assertFalse(result);
    }
    
    @Test
    void validate_whenOperators_thenThrowException() {

        final PmrvUser pmrvUser = PmrvUser.builder()
            .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
            .build();

        final UserAuthorityDTO accountOperatorAuthority =
            UserAuthorityDTO.builder().userId("operator2").authorityStatus(ACTIVE).build();
        final UserAuthoritiesDTO accountOperatorAuthorities =
            UserAuthoritiesDTO.builder()
                .authorities(List.of(accountOperatorAuthority))
                .editable(true)
                .build();

        when(operatorAuthorityQueryService.getAccountAuthorities(pmrvUser, 1L)).thenReturn(accountOperatorAuthorities);

        final boolean result = validator.areOperatorsValid(1L, Set.of("operator1"), pmrvUser);

        assertFalse(result);
    }
    
    @Test
    void areOperatorsValid_when_operators_empty_then_should_return_true() {
    	final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();

        final boolean result = validator.areOperatorsValid(1L, Collections.emptySet() , pmrvUser);

        assertThat(result).isTrue();
        verifyNoInteractions(operatorAuthorityQueryService);
    }

    @Test
    void validate_whenExternalContactsNotValid_thenThrowException() {

        final PmrvUser pmrvUser = PmrvUser.builder()
            .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
            .build();

        final CaExternalContactsDTO caExternalContactsDTO =
            CaExternalContactsDTO.builder()
                .caExternalContacts(List.of(
                    CaExternalContactDTO.builder().id(1L).email("external2").build()))
                .isEditable(false)
                .build();

        when(caExternalContactService.getCaExternalContacts(pmrvUser)).thenReturn(caExternalContactsDTO);

        final boolean result = validator.areExternalContactsValid(Set.of(10L), pmrvUser);

        assertFalse(result);
    }
    
    @Test
    void areExternalContactsValid_when_external_contacts_empty_then_should_return_true() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();

        final boolean result = validator.areExternalContactsValid(Collections.emptySet(), pmrvUser);

        assertThat(result).isTrue();
        verifyNoInteractions(caExternalContactService);
    }

    @Test
    void validate_whenSignatoryNotValid_thenThrowException() {
        
        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().accountId(1L).build())
            .build();

        when(requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(requestTask, "signatory"))
            .thenReturn(false);

        final boolean result = validator.isSignatoryValid(requestTask, "signatory");

        assertFalse(result);
    }
    
    @Test
    void isSignatoryValid_when_signatory_null_then_should_return_true() {
    	final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .build();

        final boolean result = validator.isSignatoryValid(requestTask, null);

        assertThat(result).isTrue();
        verifyNoInteractions(requestTaskAssignmentValidationService);
    }
}
