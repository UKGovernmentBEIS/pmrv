import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-site-diagram-summary-template',
  templateUrl: './site-diagram-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SiteDiagramSummaryTemplateComponent {
  @Input() cssClass: string;
  task$ = this.store.getTask('siteDiagrams');

  hasAttachments$: Observable<boolean> = this.task$.pipe(map((item) => !!item?.length));

  constructor(readonly store: PermitApplicationStore) {}
}
