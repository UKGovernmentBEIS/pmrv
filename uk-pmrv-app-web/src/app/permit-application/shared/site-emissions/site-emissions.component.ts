import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { map, Observable } from 'rxjs';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import { PermitMonitoringApproachSection } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { SiteEmissionItem } from './site-emission-item';

@Component({
  selector: 'app-site-emissions',
  templateUrl: './site-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SiteEmissionsComponent {
  displayRows: GovukSelectOption[] = [
    { text: 'Tonnes', value: true },
    { text: 'Percentage', value: false },
  ];
  form = this.formBuilder.group({ selectionControl: [true, { updateOn: 'change' }] });
  columns: GovukTableColumn[] = [
    { field: 'approach', header: 'Approach' },
    { field: 'marginal', header: 'Marginal' },
    { field: 'minimis', header: 'De-minimis' },
    { field: 'minor', header: 'Minor' },
    { field: 'major', header: 'Major' },
  ];

  tableRows$: Observable<SiteEmissionItem[]> = this.store
    .findTask('monitoringApproaches')
    .pipe(map((approaches) => this.tableData(approaches)));
  tableTotal$: Observable<SiteEmissionItem> = this.tableRows$.pipe(map((items) => items.slice(-1).pop()));

  constructor(private readonly store: PermitApplicationStore, private readonly formBuilder: FormBuilder) {}

  private tableData(approaches: { [key: string]: PermitMonitoringApproachSection }): SiteEmissionItem[] {
    const approachesData = Object.keys(approaches)
      .filter((type: PermitMonitoringApproachSection['type']) =>
        ['CALCULATION', 'FALLBACK', 'MEASUREMENT', 'N2O', 'PFC'].includes(type),
      )
      .map((type: PermitMonitoringApproachSection['type']) => this.createRow(approaches[type], type));

    return approachesData.length > 1 ? [...approachesData, this.createTotalRow(approachesData)] : approachesData;
  }

  private createTotalRow(siteEmissionItems: SiteEmissionItem[]): SiteEmissionItem {
    return {
      approach: 'Total',
      marginal: siteEmissionItems.reduce((total, item) => total + item.marginal, 0),
      minimis: siteEmissionItems.reduce((total, item) => total + item.minimis, 0),
      minor: siteEmissionItems.reduce((total, item) => total + item.minor, 0),
      major: siteEmissionItems.reduce((total, item) => total + item.major, 0),
    };
  }

  private createRow(
    approach: PermitMonitoringApproachSection,
    type: PermitMonitoringApproachSection['type'],
  ): SiteEmissionItem {
    const categories = ((approach as any).sourceStreamCategoryAppliedTiers as any[])?.map(
      (tier) => tier.sourceStreamCategory,
    );

    return {
      approach: type,
      marginal: this.approachTypeTotal(categories, 'MARGINAL'),
      minimis: this.approachTypeTotal(categories, 'DE_MINIMIS'),
      minor: this.approachTypeTotal(categories, 'MINOR'),
      major: this.approachTypeTotal(categories, 'MAJOR'),
    };
  }

  private approachTypeTotal(
    categories: {
      categoryType: 'DE_MINIMIS' | 'MAJOR' | 'MARGINAL' | 'MINOR';
      annualEmittedCO2Tonnes: number;
    }[],
    type: 'DE_MINIMIS' | 'MAJOR' | 'MARGINAL' | 'MINOR',
  ): number {
    return (categories ?? [])
      .filter((category) => category?.categoryType === type)
      .reduce((total, category) => total + (category?.annualEmittedCO2Tonnes ?? 0), 0);
  }
}
