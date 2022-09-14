package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

import java.util.Optional;

public interface AccountAuthorityInfoProvider {
    CompetentAuthority getAccountCa(Long accountId);
    Optional<Long> getAccountVerificationBodyId(Long accountId);
}
