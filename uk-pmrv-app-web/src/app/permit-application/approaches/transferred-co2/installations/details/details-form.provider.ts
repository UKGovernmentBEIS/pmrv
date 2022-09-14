import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { ReceivingTransferringInstallation } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const typeOptions: { value: ReceivingTransferringInstallation['type']; label: string }[] = [
  { value: 'RECEIVING', label: 'Receiving installation' },
  { value: 'TRANSFERRING', label: 'Transferring installation' },
];

export const detailsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const installation = route.snapshot.paramMap.get('index')
      ? (store.permit.monitoringApproaches.TRANSFERRED_CO2['receivingTransferringInstallations'][
          Number(route.snapshot.paramMap.get('index'))
        ] as ReceivingTransferringInstallation)
      : null;

    return fb.group({
      type: [installation?.type ?? null, GovukValidators.required('Select a type')],
      installationIdentificationCode: [
        installation?.installationIdentificationCode ?? null,
        GovukValidators.required('Enter an installation code'),
      ],
      operator: [installation?.operator ?? null, GovukValidators.required('Enter an operator')],
      installationName: [
        installation?.installationName ?? null,
        GovukValidators.required('Enter an installation name'),
      ],
      co2source: [installation?.co2source ?? null, GovukValidators.required('Enter a source of CO2')],
    });
  },
};
