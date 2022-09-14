import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
@Component({
  selector: 'app-emission-point-delete',
  template: `
    <app-emission-point-delete-template (delete)="onDelete()" [emissionPoint]="emissionPoint$ | async">
    </app-emission-point-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .nowrap {
        white-space: nowrap;
      }
    `,
  ],
})
export class EmissionPointDeleteComponent {
  emissionPoint$ = combineLatest([this.aerService.getTask('emissionPoints'), this.route.paramMap]).pipe(
    map(([emissionPoints, paramMap]) => emissionPoints.find((ep) => ep.id === paramMap.get('emissionPointId'))),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onDelete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('emissionPointId')),
        withLatestFrom(this.aerService.getTask('emissionPoints')),
        switchMap(([emissionPointId, emissionPoints]) =>
          this.aerService.postTaskSave(
            { emissionPoints: emissionPoints.filter((ep) => ep.id !== emissionPointId) },
            undefined,
            false,
            'emissionPoints',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
