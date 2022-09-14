package uk.gov.pmrv.api.workflow.request.flow.payment;

import java.util.EnumMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@UtilityClass
public final class RequestTypeCardPaymentDescriptionMapper {

    static final Map<RequestType, String> cardPaymentDescriptions = new EnumMap<>(RequestType.class);

    static {
        cardPaymentDescriptions.put(RequestType.PERMIT_ISSUANCE, "Pay for a permit");
        cardPaymentDescriptions.put(RequestType.PERMIT_SURRENDER, "Pay for a permit surrender");
        cardPaymentDescriptions.put(RequestType.PERMIT_REVOCATION, "Pay for a permit revocation");
    }

    public String getCardPaymentDescription(RequestType requestType) {
        return cardPaymentDescriptions.get(requestType);
    }
}
