import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EmissionPointsSummaryComponent } from './emission-points-summary.component';

describe('EmissionPointsSummaryComponent', () => {
  let component: EmissionPointsSummaryComponent;
  let fixture: ComponentFixture<EmissionPointsSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<EmissionPointsSummaryComponent> {
    get emissionPoints() {
      return this.queryAll<HTMLDListElement>('dl').map((emissionPoint) =>
        Array.from(emissionPoint.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EmissionPointsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.emissionPoints).toEqual([['The big Ref Emission point 1'], ['Yet another reference Point taken!']]);
  });
});
