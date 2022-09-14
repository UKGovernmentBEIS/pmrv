import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { map, Observable, startWith } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { siteDiagramAddFormFactory } from './site-diagram-form.provider';

@Component({
  selector: 'app-site-diagram',
  templateUrl: './site-diagram.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [siteDiagramAddFormFactory],
})
export class SiteDiagramComponent implements PendingRequest {
  isFileUploaded$: Observable<boolean> = this.form.get('siteDiagrams').valueChanges.pipe(
    startWith(this.form.get('siteDiagrams').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postTask(
        'siteDiagrams',
        this.form.value.siteDiagrams?.map((file) => file.uuid),
        true,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value.siteDiagrams?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
          },
        });

        this.store.navigate(this.route, 'summary', 'fuels');
      });
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }
}
