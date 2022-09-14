import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { filter, map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-additional-documents-summary',
  templateUrl: './additional-documents-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  files$ = this.store.getTask('additionalDocuments').pipe(
    filter((item) => !!item),
    map((item) => this.store?.getDownloadUrlFiles(item.documents)),
  );

  hasAttachments$: Observable<boolean> = this.store.getTask('additionalDocuments').pipe(
    filter((item) => !!item),
    map((item) => !!item.exist),
  );

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
