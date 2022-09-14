import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

import { GovukTableColumn } from 'govuk-components';

@Component({
  selector: 'app-emission-sources',
  templateUrl: './emission-sources.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourcesComponent implements PendingRequest {
  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  columns: GovukTableColumn[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-input--width-20' },
    { field: 'description', header: 'Description', widthClass: 'govuk-input--width-20' },
    { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];

  onSubmit(): void {
    this.aerService
      .postTaskSave({}, {}, true, 'emissionSources')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
