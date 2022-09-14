import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BreadcrumbService } from '../../../shared/breadcrumbs/breadcrumb.service';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-summary',
  template: ` <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
      </div>
    </div>

    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-page-heading>Surrender your permit</app-page-heading>
        <app-permit-surrender-summary-details></app-permit-surrender-summary-details>
        <a govukLink routerLink="../..">Return to: Permit surrender task list</a>
      </div>
    </div>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class SummaryComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((paramMap) => {
      const link = ['/permit-surrender', paramMap.get('taskId')];
      this.breadcrumbService.show([{ text: 'Permit surrender task list', link }]);
    });
  }
}
