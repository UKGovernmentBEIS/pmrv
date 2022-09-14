package uk.gov.pmrv.api.authorization.operator.service;

import uk.gov.pmrv.api.authorization.core.domain.Authority;

public interface OperatorAuthorityDeleteValidator {

    void validateDeletion(Authority authority);
}