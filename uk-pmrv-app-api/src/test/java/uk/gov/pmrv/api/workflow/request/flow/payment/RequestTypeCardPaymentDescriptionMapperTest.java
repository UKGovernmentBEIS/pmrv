package uk.gov.pmrv.api.workflow.request.flow.payment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

class RequestTypeCardPaymentDescriptionMapperTest {

    @Test
    void getCardPaymentDescription() {
        assertEquals("Pay for a permit", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_ISSUANCE));
        assertEquals("Pay for a permit surrender", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_SURRENDER));
        assertEquals("Pay for a permit revocation", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_REVOCATION));
        assertNull(RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.INSTALLATION_ACCOUNT_OPENING));
    }
}