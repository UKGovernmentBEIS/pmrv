package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionNotificationUsersValidatorTest {

    @InjectMocks
    private DecisionNotificationUsersValidator validator;
    
    @Mock
    private WorkflowUsersValidator workflowUsersValidator;
    
    @Test
    void areUsersValid_whenAllUsersValid() {
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .build();
        
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .externalContacts(Set.of(10L))
                .signatory("signatory")
                .build();
        
        final PmrvUser pmrvUser = PmrvUser.builder()
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
                .build();
        
        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator1"), pmrvUser)).thenReturn(true);
        when(workflowUsersValidator.areExternalContactsValid(Set.of(10L), pmrvUser)).thenReturn(true);
        when(workflowUsersValidator.isSignatoryValid(requestTask, "signatory")).thenReturn(true);
        
        boolean result = validator.areUsersValid(requestTask, decisionNotification, pmrvUser);

        assertThat(result).isTrue();
    }
    
    @Test
    void areUsersValid_whenOperatorNotValid() {
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .build();
        
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .externalContacts(Set.of(10L))
                .signatory("signatory")
                .build();
        
        final PmrvUser pmrvUser = PmrvUser.builder()
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
                .build();
        
        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator1"), pmrvUser)).thenReturn(false);
        
        boolean result = validator.areUsersValid(requestTask, decisionNotification, pmrvUser);

        assertThat(result).isFalse();
    }
    
    @Test
    void areUsersValid_whenExternalContactNotValid() {
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .build();
        
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .externalContacts(Set.of(10L))
                .signatory("signatory")
                .build();
        
        final PmrvUser pmrvUser = PmrvUser.builder()
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
                .build();
        
        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator1"), pmrvUser)).thenReturn(true);
        when(workflowUsersValidator.areExternalContactsValid(Set.of(10L), pmrvUser)).thenReturn(false);
        
        boolean result = validator.areUsersValid(requestTask, decisionNotification, pmrvUser);

        assertThat(result).isFalse();
    }
    
    @Test
    void areUsersValid_whenSignatoryNotValid() {
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .build();
        
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .externalContacts(Set.of(10L))
                .signatory("signatory")
                .build();
        
        final PmrvUser pmrvUser = PmrvUser.builder()
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
                .build();
        
        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator1"), pmrvUser)).thenReturn(true);
        when(workflowUsersValidator.areExternalContactsValid(Set.of(10L), pmrvUser)).thenReturn(true);
        when(workflowUsersValidator.isSignatoryValid(requestTask, "signatory")).thenReturn(false);
        
        boolean result = validator.areUsersValid(requestTask, decisionNotification, pmrvUser);

        assertThat(result).isFalse();
    }
}
