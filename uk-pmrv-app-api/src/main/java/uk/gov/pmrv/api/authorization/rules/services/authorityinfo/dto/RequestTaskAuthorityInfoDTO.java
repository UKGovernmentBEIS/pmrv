package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestTaskAuthorityInfoDTO {
	
	private String type;
	private String requestType;
	private String assignee;
	
	private ResourceAuthorityInfo authorityInfo;
}
