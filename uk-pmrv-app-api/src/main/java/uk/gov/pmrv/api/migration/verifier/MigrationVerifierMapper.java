package uk.gov.pmrv.api.migration.verifier;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.migration.verifier.domain.VerifierUserVO;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationVerifierMapper {

    AdminVerifierUserInvitationDTO toAdminVerifierUserInvitationDTO(VerifierUserVO verifier);

    @Mapping(target = "roleCode", expression = "java(verifier.getRoleCode().trim().toLowerCase().replace(\" \", \"_\"))")
    VerifierUserInvitationDTO toVerifierUserInvitationDTO(VerifierUserVO verifier);
}
