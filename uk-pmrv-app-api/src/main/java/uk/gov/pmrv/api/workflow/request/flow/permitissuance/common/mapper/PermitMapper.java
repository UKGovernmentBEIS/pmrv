package uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.GrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitMapper {

    @Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    PermitContainer toPermitContainer(PermitIssuanceRequestPayload permitIssuanceRequestPayload, InstallationOperatorDetails installationOperatorDetails);

    @AfterMapping
    default void setActivationDate(@MappingTarget PermitContainer permitContainer, PermitIssuanceRequestPayload permitIssuanceRequestPayload) {
    	if(permitIssuanceRequestPayload.getDetermination() != null && 
    			DeterminationType.GRANTED == permitIssuanceRequestPayload.getDetermination().getType()) {
            permitContainer.setActivationDate(((GrantDetermination) permitIssuanceRequestPayload.getDetermination()).getActivationDate());
        }
    }

    @AfterMapping
    default void setAnnualEmissionsTargets(@MappingTarget PermitContainer permitContainer, PermitIssuanceRequestPayload permitIssuanceRequestPayload) {
    	if(permitIssuanceRequestPayload.getDetermination() != null &&
    			DeterminationType.GRANTED == permitIssuanceRequestPayload.getDetermination().getType()) {
            permitContainer.setAnnualEmissionsTargets(((GrantDetermination) permitIssuanceRequestPayload.getDetermination()).getAnnualEmissionsTargets());
        }
    }

    PermitContainer toPermitContainer(PermitIssuanceApplicationRequestTaskPayload payload);

}
