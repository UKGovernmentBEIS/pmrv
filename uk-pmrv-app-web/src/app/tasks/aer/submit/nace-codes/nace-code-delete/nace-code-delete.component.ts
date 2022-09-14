import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { getInstallationActivityLabelByValue } from '@tasks/aer/submit/nace-codes/nace-code-types';

@Component({
  selector: 'app-source-stream-delete',
  template: `
    <ng-container *ngIf="naceCode$ | async as naceCode">
      <app-page-heading size="xl">
        Are you sure you want to delete
        <span class="nowrap"> ‘{{ getInstallationActivityLabel(naceCode) }}’? </span>
      </app-page-heading>

      <p class="govuk-body">Any reference to this item will be removed from your application.</p>

      <div class="govuk-button-group">
        <button (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
        <a govukLink routerLink="../..">Cancel</a>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NaceCodeDeleteComponent {
  naceCode$ = combineLatest([this.aerService.getTask('naceCodes'), this.route.paramMap]).pipe(
    map(([naceCodes, paramMap]) => naceCodes.codes.find((naceCode) => naceCode === paramMap.get('naceCode'))),
  );

  getInstallationActivityLabel = getInstallationActivityLabelByValue;

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onDelete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('naceCode')),
        withLatestFrom(this.aerService.getTask('naceCodes')),
        switchMap(([naceCode, naceCodes]) =>
          this.aerService.postTaskSave(
            {
              naceCodes: {
                codes: naceCodes.codes.filter((code) => code !== naceCode),
              },
            },
            undefined,
            false,
            'naceCodes',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
