package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

public interface DocumentTemplateAuthorityInfoProvider {
    CompetentAuthority getDocumentTemplateCaById(Long templateId);
}
