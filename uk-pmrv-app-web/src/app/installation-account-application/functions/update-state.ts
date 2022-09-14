import { LocationOffShoreDTO, LocationOnShoreDTO } from 'pmrv-api';

import { ResponsibilityOption } from '../confirm-responsibility/responsibility';
import { EtsScheme } from '../ets-scheme/ets-scheme';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

export function updateState(store: InstallationAccountApplicationStore, request: any, taskId?: number): void {
  store.setState({ ...store.getState(), isReviewed: true, taskId });
  store.updateTask(
    ApplicationSectionType.responsibility,
    {
      responsibility: [
        ResponsibilityOption.control,
        ResponsibilityOption.effectivePermit,
        ResponsibilityOption.management,
      ],
    },
    'complete',
  );
  store.updateTask(
    ApplicationSectionType.installation,
    {
      installationTypeGroup: { type: request.location.type },
      locationGroup: {
        location: request.competentAuthority === 'OPRED' ? null : request.competentAuthority,
      },
      ...(isOnshore(request.location)
        ? {
            onshoreGroup: {
              name: request.name,
              siteName: request.siteName,
              address: request.location.address,
              gridReference: request.location.gridReference,
            },
          }
        : {
            offshoreGroup: {
              name: request.name,
              siteName: request.siteName,
              latitude: request.location.latitude,
              longitude: request.location.longitude,
            },
          }),
    },
    'complete',
  );

  store.updateTask(
    ApplicationSectionType.legalEntity,
    {
      selectGroup: { id: request.legalEntity.id ?? null, isNew: !request.legalEntity.id },
      detailsGroup: {
        name: request.legalEntity.name,
        address: request.legalEntity.address,
        noReferenceNumberReason: request.legalEntity.noReferenceNumberReason ?? null,
        referenceNumber: request.legalEntity.referenceNumber,
        type: request.legalEntity.type,
      },
    },
    'complete',
  );

  store.updateTask(
    ApplicationSectionType.etsScheme,
    { etsSchemeType: request.emissionTradingScheme as EtsScheme['etsSchemeType'] },
    'complete',
  );

  store.updateTask(
    ApplicationSectionType.commencement,
    { commencementDate: new Date(request.commencementDate) },
    'complete',
  );
}

function isOnshore(location: LocationOnShoreDTO | LocationOffShoreDTO): location is LocationOnShoreDTO {
  return location.type === 'ONSHORE';
}
