package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

public interface PermitAuthorityInfoProvider {
    Long getPermitAccountById(String id);
}
