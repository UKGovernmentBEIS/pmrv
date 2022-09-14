package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;


@Mapper(componentModel = "spring", uses = {LocationMapper.class, LegalEntityMapper.class}, config = MapperConfig.class)
public interface AccountPayloadMapper {

	AccountDTO toAccountDTO(AccountPayload accountPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)")
	InstallationAccountOpeningRequestPayload toInstallationAccountOpeningRequestPayload(AccountPayload accountPayload, String operatorAssignee);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD)")
    InstallationAccountOpeningApplicationSubmittedRequestActionPayload toInstallationAccountOpeningApplicationSubmittedRequestActionPayload(AccountPayload accountPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)")
    InstallationAccountOpeningApplicationRequestTaskPayload toInstallationAccountOpeningApplicationRequestTaskPayload(AccountPayload accountPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD)")
    InstallationAccountOpeningApprovedRequestActionPayload toInstallationAccountOpeningApprovedRequestActionPayload(AccountPayload accountPayload);
}
