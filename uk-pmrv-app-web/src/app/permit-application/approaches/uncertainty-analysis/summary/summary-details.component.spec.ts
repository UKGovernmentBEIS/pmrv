import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitApplicationModule } from '../../../permit-application.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState } from '../../../testing/mock-state';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let store: PermitApplicationStore;
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<SummaryDetailsComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'uncertaintyAnalysis' });

  class Page extends BasePage<SummaryDetailsComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(SummaryDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary details', () => {
    expect(page.answers).toEqual([
      ['Are you supplying an uncertainty analysis as part of your permit application?', 'Yes'],
      ['Uploaded files', 'uncertainty.pdf'],
    ]);
  });
});
