import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { NotificationTemplatesService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass, MockType } from '../../../testing';
import { mockedEmailTemplate } from '../testing/mock-data';
import { EmailTemplateGuard } from './email-template.guard';

describe('EmailTemplateGuard', () => {
  let guard: EmailTemplateGuard;
  let notificationTemplatesService: MockType<NotificationTemplatesService>;

  beforeEach(() => {
    notificationTemplatesService = mockClass(NotificationTemplatesService);
    notificationTemplatesService.getNotificationTemplateByIdUsingGET.mockReturnValueOnce(of(mockedEmailTemplate));

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        EmailTemplateGuard,
        { provide: NotificationTemplatesService, useValue: notificationTemplatesService },
      ],
    });
    guard = TestBed.inject(EmailTemplateGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return email template', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ templateId: mockedEmailTemplate.id }))),
    ).resolves.toBeTruthy();

    expect(notificationTemplatesService.getNotificationTemplateByIdUsingGET).toHaveBeenCalledWith(
      mockedEmailTemplate.id,
    );

    await expect(guard.resolve()).toEqual(mockedEmailTemplate);
  });
});
