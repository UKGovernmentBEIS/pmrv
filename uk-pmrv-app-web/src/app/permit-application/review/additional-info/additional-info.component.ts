import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, map, pluck } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-additional-info',
  templateUrl: './additional-info.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalInfoComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  groupKey$ = this.route.data.pipe(pluck('groupKey'));

  files$ = this.store.getTask('additionalDocuments').pipe(
    filter((item) => !!item),
    map((item) => this.store?.getDownloadUrlFiles(item.documents)),
  );

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
