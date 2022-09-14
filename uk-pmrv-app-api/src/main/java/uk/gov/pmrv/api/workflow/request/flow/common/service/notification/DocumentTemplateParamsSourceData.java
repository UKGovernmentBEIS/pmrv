package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplateParamsSourceData {
    
    private DocumentTemplateGenerationContextActionType contextActionType;
    private Request request;
    private String signatory;
    private UserInfoDTO accountPrimaryContact;
    
    private String toRecipientEmail;
    @Builder.Default
    private List<String> ccRecipientsEmails = new ArrayList<>();
}
