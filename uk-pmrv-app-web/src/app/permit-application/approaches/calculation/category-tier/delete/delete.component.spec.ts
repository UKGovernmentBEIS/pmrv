import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockState } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '237', index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
  });

  class Page extends BasePage<DeleteComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, CalculationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual(
      'Are you sure you want to delete  ‘13123124 White Spirit & SBP: Major’?',
    );

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild({
        monitoringApproaches: {
          ...mockState.permit.monitoringApproaches,
          CALCULATION: {
            ...mockState.permit.monitoringApproaches.CALCULATION,
            sourceStreamCategoryAppliedTiers: null,
          } as CalculationMonitoringApproach,
        },
      }),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });
});
