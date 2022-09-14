import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { filter, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  files$ = this.aerService.getTask('additionalDocuments').pipe(
    filter((item) => !!item),
    map((item) => this.aerService?.getDownloadUrlFiles(item.documents)),
  );

  hasAttachments$: Observable<boolean> = this.files$.pipe(map((files) => (files || []).length > 0));

  constructor(readonly aerService: AerService, private readonly router: Router) {}
}
