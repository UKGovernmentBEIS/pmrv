import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PermitApplicationStore } from '../store/permit-application.store';

@Component({
  selector: 'app-emission-sources',
  templateUrl: './emission-sources.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourcesComponent implements PendingRequest {
  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postStatus('emissionSources', true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'fuels'));
  }
}
