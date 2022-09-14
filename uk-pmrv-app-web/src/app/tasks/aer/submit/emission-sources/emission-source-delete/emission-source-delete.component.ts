import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-emission-source-delete',
  template: `
    <app-emission-source-delete-template
      [emissionSource]="emissionSource$ | async"
      (delete)="delete()"
    ></app-emission-source-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourceDeleteComponent {
  emissionSource$ = combineLatest([this.aerService.getTask('emissionSources'), this.route.paramMap]).pipe(
    map(([emissionSources, paramMap]) => emissionSources.find((source) => source.id === paramMap.get('sourceId'))),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly aerService: AerService,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('sourceId')),
        withLatestFrom(this.aerService.getTask('emissionSources')),
        switchMap(([streamId, emissionSources]) =>
          this.aerService.postTaskSave(
            { emissionSources: emissionSources.filter((stream) => stream.id !== streamId) },
            undefined,
            false,
            'emissionSources',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
