import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { PermitTypeSummaryTemplateComponent } from './permit-type-summary-template.component';

describe('PermitTypeSummaryTemplateComponent', () => {
  let store: PermitApplicationStore;
  let component: PermitTypeSummaryTemplateComponent;
  let fixture: ComponentFixture<PermitTypeSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<PermitTypeSummaryTemplateComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(PermitTypeSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary details', () => {
    expect(page.answers).toEqual([['Type', 'GHGE']]);
  });
});
