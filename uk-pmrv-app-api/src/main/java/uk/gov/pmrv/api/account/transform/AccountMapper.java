package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountHeaderInfoDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(
	componentModel = "spring",
	uses = {LocationMapper.class, LegalEntityMapper.class},
	config = MapperConfig.class
)
public interface AccountMapper {

	@Mapping(target = "id", source = "identifier")
	@Mapping(target = "emitterId", expression = "java(\"EM\" + String.format(\"%05d\", identifier))")
	Account toAccount(AccountDTO accountDTO, Long identifier);

	AccountDTO toAccountDTO(Account account);

	@Mapping(target = "legalEntityName", source = "legalEntity.name")
	@Mapping(target = "legalEntityType", source = "legalEntity.type")
	@Mapping(target = "companyReferenceNumber", source = "legalEntity.referenceNumber")
	@Mapping(target = "legalEntityAddress", source = "legalEntity.location.address")
	@Mapping(target = "applicationType", expression = "java(uk.gov.pmrv.api.account.domain.enumeration.ApplicationType.NEW_PERMIT)")
	AccountDetailsDTO toAccountDetailsDTO(Account account);

	AccountHeaderInfoDTO toAccountHeaderInfoDTO(Account account);

}
