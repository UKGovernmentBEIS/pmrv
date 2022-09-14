package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTemplateParams {

    private String name;
    private String siteName;
    private String location;
    private String emitterType;
    
    private String legalEntityName;
    private String legalEntityLocation;
    
    private String primaryContact; //full name
    private String serviceContact; //full name
    
}
