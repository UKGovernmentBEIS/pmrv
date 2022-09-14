package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

class RequestTypeDocumentTemplateInfoMapperTest {

    @Test
    void test() {
        assertThat(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(RequestType.PERMIT_ISSUANCE)).isEqualTo(RequestType.PERMIT_ISSUANCE.getDescription());
        assertThat(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(RequestType.PERMIT_SURRENDER)).isEqualTo("your application to surrender/rationalise your permit");
        assertThat(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(RequestType.PERMIT_NOTIFICATION)).isEqualTo("your AEM Permit Notification");
        assertThat(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(RequestType.PERMIT_VARIATION)).isEqualTo("your application for a permit variation");
        assertThat(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(RequestType.SYSTEM_MESSAGE_NOTIFICATION)).isEqualTo("N/A");
    }
}
