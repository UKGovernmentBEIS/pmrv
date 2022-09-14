import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-source-stream-delete',
  template: `
    <app-source-stream-delete-template (delete)="onDelete()" [sourceStream]="sourceStream$ | async">
    </app-source-stream-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamDeleteComponent {
  sourceStream$ = combineLatest([this.aerService.getTask('sourceStreams'), this.route.paramMap]).pipe(
    map(([sourceStreams, paramMap]) =>
      sourceStreams.find((sourceStream) => sourceStream.id === paramMap.get('streamId')),
    ),
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
        map((paramMap) => paramMap.get('streamId')),
        withLatestFrom(this.aerService.getTask('sourceStreams')),
        switchMap(([streamId, sourceStreams]) =>
          this.aerService.postTaskSave(
            { sourceStreams: sourceStreams.filter((stream) => stream.id !== streamId) },
            undefined,
            false,
            'sourceStreams',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
