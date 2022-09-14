import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-emission-summary-delete',
  template: `
    <app-page-heading size="xl"> Are you sure you want to delete this emission summary?</app-page-heading>

    <p class="govuk-body">Any reference to this item will be removed from your application.</p>

    <div class="govuk-button-group">
      <button appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSummaryDeleteComponent {
  constructor(
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => Number(paramMap.get('emissionSummaryIndex'))),
        withLatestFrom(this.store.getTask('emissionSummaries')),
        map(([index, emissionSummaries]) => emissionSummaries.filter((_, i) => i !== index)),
        switchMap((summaries) => this.store.postTask('emissionSummaries', summaries, false)),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
