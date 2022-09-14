import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { SharedPermitModule } from '../shared-permit.module';
import { SiteEmissionsComponent } from './site-emissions.component';

describe('SiteEmissionsComponent', () => {
  let page: Page;
  let component: SiteEmissionsComponent;
  let fixture: ComponentFixture<SiteEmissionsComponent>;
  let store: PermitApplicationStore;

  class Page extends BasePage<SiteEmissionsComponent> {
    set selectValue(value: string) {
      this.setInputValue('select', value);
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SiteEmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedPermitModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    }).compileComponents();
  });

  describe('with all monitoring approaches', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('show site emissions table', () => {
      expect(page.rows).toEqual([
        ['Calculation', '0t', '0t', '0t', '23.5t'],
        ['Perfluorocarbons (PFC)', '0t', '0t', '0t', '23.5t'],
        ['Nitrous oxide (N2O)', '0t', '0t', '0t', '23.5t'],
        ['Fall-back', '0t', '0t', '0t', '23.8t'],
        ['Measurement', '0t', '0t', '0t', '23.8t'],
        ['Total', '0t', '0t', '0t', '118.1t'],
      ]);

      page.selectValue = 'false';
      fixture.detectChanges();

      expect(page.rows).toEqual([
        ['Calculation', '0%', '0%', '0%', '20%'],
        ['Perfluorocarbons (PFC)', '0%', '0%', '0%', '20%'],
        ['Nitrous oxide (N2O)', '0%', '0%', '0%', '20%'],
        ['Fall-back', '0%', '0%', '0%', '20%'],
        ['Measurement', '0%', '0%', '0%', '20%'],
        ['Total', '0%', '0%', '0%', '100%'],
      ]);
    });
  });

  describe('with missing monitoring approaches tiers', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            FALLBACK: {},
          },
        }),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('show site emissions table', () => {
      expect(page.rows).toEqual([
        ['Calculation', '0t', '0t', '0t', '23.5t'],
        ['Perfluorocarbons (PFC)', '0t', '0t', '0t', '23.5t'],
        ['Nitrous oxide (N2O)', '0t', '0t', '0t', '23.5t'],
        ['Fall-back', '0t', '0t', '0t', '0t'],
        ['Measurement', '0t', '0t', '0t', '23.8t'],
        ['Total', '0t', '0t', '0t', '94.3t'],
      ]);

      page.selectValue = 'false';
      fixture.detectChanges();

      expect(page.rows).toEqual([
        ['Calculation', '0%', '0%', '0%', '25%'],
        ['Perfluorocarbons (PFC)', '0%', '0%', '0%', '25%'],
        ['Nitrous oxide (N2O)', '0%', '0%', '0%', '25%'],
        ['Fall-back', '0%', '0%', '0%', '0%'],
        ['Measurement', '0%', '0%', '0%', '25%'],
        ['Total', '0%', '0%', '0%', '100%'],
      ]);
    });
  });

  describe('with one monitoring approach defined with no values', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            PFC: {},
          },
        }),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('show site emissions table', () => {
      expect(page.rows).toEqual([['Perfluorocarbons (PFC)', '0t', '0t', '0t', '0t']]);

      page.selectValue = 'false';
      fixture.detectChanges();

      expect(page.rows).toEqual([['Perfluorocarbons (PFC)', '0%', '0%', '0%', '0%']]);
    });
  });
});
