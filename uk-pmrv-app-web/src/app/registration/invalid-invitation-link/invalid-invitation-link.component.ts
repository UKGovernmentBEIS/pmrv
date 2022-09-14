import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, map, tap } from 'rxjs';

@Component({
  selector: 'app-invalid-invitation-link',
  template: `
    <ng-container [ngSwitch]="error$ | async">
      <app-page-heading>{{ title$ | async }}</app-page-heading>
      <ng-container *ngSwitchCase="'EMAIL1001'">
        <p class="govuk-body">
          Please contact the admin for the installation you are seeking to access and request that they add you once
          again as a new user.
        </p>
        <p class="govuk-body">
          When the admin user has done this you will receive a new email with a link enabling you to create your
          account.
        </p>
      </ng-container>
      <ng-container *ngSwitchDefault>
        <p class="govuk-body">Please contact the admin for the installation you are seeking to access.</p>
      </ng-container>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidInvitationLinkComponent {
  title$ = new BehaviorSubject<string>('');

  error$ = this.activatedRoute.queryParamMap.pipe(
    map((params) => params.get('code')),
    tap((code) => {
      let title: string;

      if (code === 'EMAIL1001') {
        title = 'This link has expired';
      } else {
        title = 'This link is invalid';
      }

      this.title$.next(title);
      this.titleService.setTitle(title);
    }),
  );

  constructor(private readonly activatedRoute: ActivatedRoute, private readonly titleService: Title) {}
}
