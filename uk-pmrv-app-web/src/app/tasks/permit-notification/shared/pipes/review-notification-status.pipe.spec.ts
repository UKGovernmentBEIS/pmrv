import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationService } from '../../core/permit-notification.service';
import { ReviewNotificationStatusPipe } from './review-notification-status.pipe';

describe('ReviewNotificationStatusPipe', () => {
  let pipe: ReviewNotificationStatusPipe;

  let permitNotificationService: PermitNotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [ReviewNotificationStatusPipe],
      providers: [KeycloakService],
    });

    permitNotificationService = TestBed.inject(PermitNotificationService);
  });

  beforeEach(() => (pipe = new ReviewNotificationStatusPipe(permitNotificationService)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve accepted decision status for notification', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD',
        reviewDecision: {
          type: 'ACCEPTED',
          officialNotice: 'Laborum officiis eiu',
          followUp: {
            followUpResponseRequired: true,
            followUpRequest: 'xgdfg',
            followUpResponseExpirationDate: '2033-01-01',
          },
          notes: 'dfgfg',
        },
        permitNotification: {
          type: 'OTHER_FACTOR',
          description: 'description',
          reportingType: 'RENOUNCE_FREE_ALLOCATIONS',
        },
      }),
    );
    await expect(firstValueFrom(pipe.transform('DETAILS_CHANGE'))).resolves.toEqual('accepted');
  });

  it('should resolve rejected decision status for notification', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD',
        reviewDecision: {
          type: 'REJECTED',
          officialNotice: 'sdfsd',
          notes: 'sdfs',
        },
        permitNotification: {
          type: 'OTHER_FACTOR',
          description: 'description',
          reportingType: 'RENOUNCE_FREE_ALLOCATIONS',
        },
      }),
    );
    await expect(firstValueFrom(pipe.transform('DETAILS_CHANGE'))).resolves.toEqual('rejected');
  });

  it('should resolve accepted decision status for follow up', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
        reviewDecision: {
          type: 'ACCEPTED',
        },
      }),
    );
    await expect(firstValueFrom(pipe.transform('FOLLOW_UP'))).resolves.toEqual('accepted');
  });

  it('should resolve accepted decision status for follow up', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
        reviewDecision: {
          type: 'AMENDS_NEEDED',
        },
      }),
    );
    await expect(firstValueFrom(pipe.transform('FOLLOW_UP'))).resolves.toEqual('operator to amend');
  });
});
