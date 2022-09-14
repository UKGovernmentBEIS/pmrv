import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { TransferredCO2Component } from './transferred-co2.component';
import { TransferredCO2Module } from './transferred-co2.module';

describe('TransferredCO2Component', () => {
  let component: TransferredCO2Component;
  let fixture: ComponentFixture<TransferredCO2Component>;
  let page: Page;

  class Page extends BasePage<TransferredCO2Component> {
    get staticSections(): HTMLAnchorElement[] {
      return Array.from(this.queryAll<HTMLAnchorElement>('ul > li > span.app-task-list__task-name > a'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule, TransferredCO2Module],
    }).compileComponents();
  });

  beforeEach(() => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: mockPermitCompletePayload.permitSectionsCompleted,
    });
    fixture = TestBed.createComponent(TransferredCO2Component);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the sections', () => {
    expect(page.staticSections.map((el) => el.textContent.trim())).toEqual(
      expect.arrayContaining([
        'Receiving and transferring installations',
        'Accounting for emissions from transferred CO2',
        'Deductions to amount of transferred CO2',
        'Procedure for leakage events',
        'Temperature and pressure measurement equipment',
        'Transfer of CO2',
        'Quantification methodologies',
        'Approach description',
      ]),
    );
  });
});
