package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@UtilityClass
public class RequestTypeDocumentTemplateInfoMapper {

    private static Map<RequestType, String> map = new HashMap<>();
    
    static {
        map.put(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_ISSUANCE.getDescription());
        map.put(RequestType.PERMIT_SURRENDER, "your application to surrender/rationalise your permit");
        map.put(RequestType.PERMIT_NOTIFICATION, "your AEM Permit Notification");
        map.put(RequestType.PERMIT_VARIATION, "your application for a permit variation");
    }
    
    public String getTemplateInfo(RequestType requestType) {
        return map.getOrDefault(requestType, "N/A");
    }
}
