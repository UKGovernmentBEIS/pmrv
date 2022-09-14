import { Pipe, PipeTransform } from '@angular/core';

export const vbTypes = ['UK_ETS_INSTALLATIONS', 'EU_ETS_INSTALLATIONS', 'UK_ETS_AVIATION', 'CORSIA'] as const;
export type VerificationBodyType = typeof vbTypes[number];

@Pipe({ name: 'verificationBodyType' })
export class VerificationBodyTypePipe implements PipeTransform {
  transform(value: VerificationBodyType): string {
    switch (value) {
      case 'UK_ETS_INSTALLATIONS':
        return 'UK ETS Installations';
      case 'EU_ETS_INSTALLATIONS':
        return 'EU ETS Installations';
      case 'UK_ETS_AVIATION':
        return 'UK ETS Aviation';
      case 'CORSIA':
        return 'CORSIA';
      default:
        return null;
    }
  }
}
