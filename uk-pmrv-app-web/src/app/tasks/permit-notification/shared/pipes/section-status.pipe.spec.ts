import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { SharedModule } from '../../../../shared/shared.module';
import { PermitNotificationService } from '../../core/permit-notification.service';
import { SectionStatusPipe } from './section-status.pipe';

describe('SectionStatusPipe', () => {
  let pipe: SectionStatusPipe;
  let permitNotificationService: PermitNotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [SectionStatusPipe],

      providers: [KeycloakService],
    });

    permitNotificationService = TestBed.inject(PermitNotificationService);
  });

  beforeEach(() => (pipe = new SectionStatusPipe(permitNotificationService)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve status', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
        sectionsCompleted: {},
      } as PermitNotificationApplicationSubmitRequestTaskPayload),
    );

    await expect(firstValueFrom(pipe.transform('DETAILS_CHANGE'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('SUBMIT'))).resolves.toEqual('cannot start yet');

    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
        permitNotification: {
          type: 'OTHER_FACTOR',
          description: 'description',
        },
        sectionsCompleted: {
          DETAILS_CHANGE: true,
        },
      } as PermitNotificationApplicationSubmitRequestTaskPayload),
    );

    await expect(firstValueFrom(pipe.transform('DETAILS_CHANGE'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('SUBMIT'))).resolves.toEqual('not started');

    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
        permitNotification: {
          type: 'OTHER_FACTOR',
          description: 'description',
        },
        sectionsCompleted: {
          DETAILS_CHANGE: false,
        },
      } as PermitNotificationApplicationSubmitRequestTaskPayload),
    );

    await expect(firstValueFrom(pipe.transform('DETAILS_CHANGE'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('SUBMIT'))).resolves.toEqual('cannot start yet');
  });
});
