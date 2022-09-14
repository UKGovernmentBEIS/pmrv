import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { getInstallationActivityLabelByValue } from '@tasks/aer/submit/nace-codes/nace-code-types';

import { GovukTableColumn } from 'govuk-components';

@Component({
  selector: 'app-source-streams',
  templateUrl: './nace-codes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NaceCodesComponent implements PendingRequest {
  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  naceCodes$ = this.aerService
    .getTask('naceCodes')
    .pipe(
      map((naceCodes) =>
        naceCodes?.codes?.map((val) => ({ code: val, label: getInstallationActivityLabelByValue(val) })),
      ),
    );

  columns: GovukTableColumn[] = [
    { field: 'mainActivity', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'label', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];

  onSubmit(): void {
    this.aerService
      .postTaskSave({}, {}, true, 'naceCodes')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
