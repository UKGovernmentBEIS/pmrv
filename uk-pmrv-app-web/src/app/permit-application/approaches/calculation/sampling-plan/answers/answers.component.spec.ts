import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockState } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'CALCULATION_Plan' });

  class Page extends BasePage<AnswersComponent> {
    get confirm(): HTMLButtonElement {
      return this.query('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should confirm the answers', () => {
    page.confirm.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(undefined, { CALCULATION_Plan: [true] }),
    );
  });
});
