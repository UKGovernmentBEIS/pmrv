import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';

import { permitRevocationMapper } from '@permit-revocation/constants/permit-revocation-consts';
import { summaryList } from '@permit-revocation/core/model/permit-revocation-models';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { isEmpty, some } from 'lodash';
import moment from 'moment';

import { PermitRevocation } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  providers: [GovukDatePipe],
  styles: ['.govuk-list-error-key { border-left: 5px solid #d4351c; padding-left: 10px}'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  @Input() changeRoute = true;
  @Input() errors: ValidationErrors;
  @Input() sectionHeading: string = undefined;
  permitRevocation$: Observable<any> = this.store.getPermitRevocation();
  readonly permitRevocationMapper = permitRevocationMapper;

  constructor(readonly store: PermitRevocationStore, readonly route: ActivatedRoute, readonly date: GovukDatePipe) {}

  getError(errors: ValidationErrors, errorKey: string, value?: any) {
    return value
      ? errors && errors[errorKey] && moment(value, 'YYYY-MM-DD', true).isValid()
      : errors && errors[errorKey];
  }

  getValue(value: any) {
    const type = typeof value;
    switch (type) {
      case 'boolean':
        return value ? null : 'No';
      case 'string':
        return moment(value, 'YYYY-MM-DD', true).isValid() ? this.date.transform(value) : value;
      case 'object':
        return this.date.transform(value);
    }
  }

  isArray(value) {
    return Array.isArray(value);
  }

  summaryList(permitRevocation: PermitRevocation): summaryList[] {
    let summaryList: summaryList[] = [];

    const stopLabel = permitRevocationMapper['stoppedDate'].label;
    const emissionLabel = permitRevocationMapper['annualEmissionsReportDate'].label;
    const surrenderLabel = permitRevocationMapper['surrenderDate'].label;
    const feeLabel = permitRevocationMapper['feeDate'].label;
    const effectiveLabel = permitRevocationMapper['effectiveDate'].label;

    const stop = { key: null, value: [] };
    const emissions = { key: null, value: [] };
    const surrender = { key: null, value: [] };
    const fee = { key: null, value: [] };
    const effective = { key: null, value: [] };

    for (const [prop, val] of Object.entries(permitRevocation)) {
      if (permitRevocationMapper[prop].label.trim() === stopLabel.trim()) {
        stop.key = prop;
        stop.value = [...stop.value, val];
      } else if (permitRevocationMapper[prop].label.trim() === emissionLabel.trim()) {
        emissions.key = prop;
        emissions.value = [...emissions.value, val];
      } else if (permitRevocationMapper[prop].label.trim() === surrenderLabel.trim()) {
        surrender.key = prop;
        surrender.value = [...surrender.value, val];
      } else if (permitRevocationMapper[prop].label.trim() === feeLabel.trim()) {
        fee.key = prop;
        fee.value = [...fee.value, val];
      } else if (permitRevocationMapper[prop].label.trim() === effectiveLabel.trim()) {
        effective.key = prop;
        effective.value = [...effective.value, val];
      } else {
        summaryList = [...summaryList, { key: prop, value: val }];
      }
    }

    if (!some(stop, isEmpty)) {
      summaryList.push(stop);
    }
    if (!some(emissions, isEmpty)) {
      summaryList.push(emissions);
    }
    if (!some(surrender, isEmpty)) {
      summaryList.push(surrender);
    }
    if (!some(fee, isEmpty)) {
      summaryList.push(fee);
    }
    if (!some(effective, isEmpty)) {
      summaryList.push(effective);
    }

    return summaryList.sort((a, b) => permitRevocationMapper[a.key].step - permitRevocationMapper[b.key].step);
  }
}
