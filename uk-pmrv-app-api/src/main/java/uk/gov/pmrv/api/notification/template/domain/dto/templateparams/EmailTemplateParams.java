package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplateParams {
    
    @NotBlank
    private String toRecipient;
    
    @Builder.Default
    private List<String> ccRecipients = new ArrayList<>();
}
