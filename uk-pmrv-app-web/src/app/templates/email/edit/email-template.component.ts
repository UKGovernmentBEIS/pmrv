import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  filter,
  first,
  map,
  Observable,
  of,
  pluck,
  shareReplay,
  switchMap,
  tap,
} from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { NotificationTemplateDTO, NotificationTemplatesService } from 'pmrv-api';

import { BackLinkService } from '../../../shared/back-link/back-link.service';

@Component({
  selector: 'app-email-template',
  templateUrl: './email-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class EmailTemplateComponent implements OnInit {
  emailTemplate$: Observable<NotificationTemplateDTO> = this.route.data.pipe(pluck('emailTemplate'));

  form$ = this.emailTemplate$.pipe(
    map((emailTemplate) =>
      this.fb.group({
        subject: [
          emailTemplate?.subject,
          [
            GovukValidators.required('Enter an email subject'),
            GovukValidators.maxLength(255, 'The email subject should not be more than 255 characters'),
          ],
        ],
        message: [
          emailTemplate?.text,
          [
            GovukValidators.required('Enter an email message'),
            GovukValidators.maxLength(10000, 'The email message should not be more than 10000 characters'),
          ],
        ],
      }),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly notificationTemplatesService: NotificationTemplatesService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.backLinkService.show();
  }

  onSubmit(): void {
    combineLatest([this.form$, this.emailTemplate$])
      .pipe(
        first(),
        tap(([form]) => {
          if (!form.valid) {
            this.displayErrorSummary$.next(true);
          }
        }),
        filter(([form]) => form.valid),
        switchMap(([form, emailTemplate]) =>
          form.dirty
            ? this.notificationTemplatesService.updateNotificationTemplateUsingPUT(emailTemplate.id, {
                subject: form.get('subject').value,
                text: form.get('message').value,
              })
            : of(null),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } }));
  }
}
