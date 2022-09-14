import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { MeasurementComponent } from './measurement.component';
import { MeasurementModule } from './measurement.module';

describe('MeasurementComponent', () => {
  let page: Page;
  let component: MeasurementComponent;
  let fixture: ComponentFixture<MeasurementComponent>;
  let store: PermitApplicationStore;

  class Page extends BasePage<MeasurementComponent> {
    get approachDetailsSubTasks(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li'));
    }
    get approachDetailsSubTaskDescriptions() {
      return Array.from(this.approachDetailsSubTasks).map((li) =>
        li.querySelector<HTMLSpanElement>('span').textContent.trim(),
      );
    }
    getApproachDetailsSubTaskStatus(value) {
      return Array.from(this.approachDetailsSubTasks)
        .find((li) => li.querySelector<HTMLSpanElement>('span').textContent.trim() === value)
        .querySelector<HTMLElement>('govuk-tag strong')
        .textContent.trim();
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeasurementModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild({
        ...mockPermitApplyPayload.permit,
        monitoringApproaches: {
          MEASUREMENT: {},
        },
      }),
    );

    fixture = TestBed.createComponent(MeasurementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display list of sub tasks', () => {
    expect(page.rows).toEqual([['Measurement', '0t', '0t', '0t', '0t']]);
    expect(page.approachDetailsSubTaskDescriptions).toEqual([
      'Add a source stream category',
      'Approach description',
      'Determination of emissions by measurement',
      'Determination of reference period',
      'Calculation of gas flow',
      'Biomass emissions',
      'Corroborating calculations',
    ]);
    expect(page.getApproachDetailsSubTaskStatus('Approach description')).toEqual('not started');
    expect(page.getApproachDetailsSubTaskStatus('Determination of emissions by measurement')).toEqual('not started');
    expect(page.getApproachDetailsSubTaskStatus('Determination of reference period')).toEqual('not started');
    expect(page.getApproachDetailsSubTaskStatus('Calculation of gas flow')).toEqual('not started');
    expect(page.getApproachDetailsSubTaskStatus('Biomass emissions')).toEqual('not started');
    expect(page.getApproachDetailsSubTaskStatus('Corroborating calculations')).toEqual('not started');
  });
});
