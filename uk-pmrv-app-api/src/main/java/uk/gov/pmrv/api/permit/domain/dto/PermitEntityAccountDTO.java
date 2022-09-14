package uk.gov.pmrv.api.permit.domain.dto;

import uk.gov.pmrv.api.permit.domain.PermitType;

public interface PermitEntityAccountDTO {

    String getPermitEntityId();

    Long getAccountId();

    PermitType getPermitType();
}
