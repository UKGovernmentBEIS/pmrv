import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../../testing';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockState } from '../../../../../testing/mock-state';
import { MeasurementModule } from '../../../measurement.module';
import { CategorySummaryComponent } from './category-summary.component';

describe('CategorySummaryComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: CategorySummaryComponent;
  let fixture: ComponentFixture<CategorySummaryComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '237', index: '0' });

  class Page extends BasePage<CategorySummaryComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((dd) => dd.textContent.trim());
    }
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeasurementModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    fixture = TestBed.createComponent(CategorySummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the source stream category summary', () => {
    expect(page.summaryDefinitions).toEqual([
      '13123124 White Spirit & SBP: Major',
      'S1 Boiler',
      'The big Ref Emission point 1',
      '23.8 tonnes',
    ]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
