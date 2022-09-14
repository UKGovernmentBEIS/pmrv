import { Pipe, PipeTransform } from '@angular/core';

import { ReceivingTransferringInstallation } from 'pmrv-api';

import { typeOptions } from './details/details-form.provider';

@Pipe({ name: 'installationType' })
export class InstallationTypePipe implements PipeTransform {
  transform(value: ReceivingTransferringInstallation['type']): string {
    return typeOptions.find((option) => option.value === value).label;
  }
}
