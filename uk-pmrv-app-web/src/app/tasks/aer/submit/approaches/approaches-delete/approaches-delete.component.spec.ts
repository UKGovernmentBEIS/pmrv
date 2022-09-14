import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ApproachesDeleteComponent } from './approaches-delete.component';

describe('ApproachesDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: ApproachesDeleteComponent;
  let fixture: ComponentFixture<ApproachesDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub(
    {
      monitoringApproach: mockAerApplyPayload.aer.monitoringApproachTypes[0],
    },
    {},
    { permitTask: 'monitoringApproaches' },
  );

  class Page extends BasePage<ApproachesDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get body() {
      return this.query<HTMLElement>('.govuk-body');
    }

    get deleteButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(ApproachesDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the name of the approach to be deleted', () => {
    expect(page.header.textContent.trim()).toContain("Are you sure you want to delete 'Calculation'?");
    expect(page.body.textContent.trim()).toContain(
      'All information related to the Calculation approach will be deleted.',
    );
  });

  it('should delete the monitoring approach', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    const storedMonitoringApproaches = mockAerApplyPayload.aer.monitoringApproachTypes;
    const storedStatusesCompleted = mockAerApplyPayload.aerSectionsCompleted;
    const deletedMonitoringApproach = activatedRoute.snapshot.paramMap.get('monitoringApproach');

    const remainingMonitoringApproaches = storedMonitoringApproaches.filter((key) => key !== deletedMonitoringApproach);

    const remainingStatusesCompleted = Object.keys(storedStatusesCompleted)
      .filter((key) => key !== deletedMonitoringApproach)
      .reduce((res, key) => ({ ...res, [key]: storedStatusesCompleted[key] }), {});

    page.deleteButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_SAVE_APPLICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        aerSectionsCompleted: {
          ...remainingStatusesCompleted,
          monitoringApproachTypes: [false],
        },
        payloadType: 'AER_SAVE_APPLICATION_PAYLOAD',
        aer: {
          ...(mockState.requestTaskItem.requestTask.payload as any).aer,
          monitoringApproachTypes: remainingMonitoringApproaches,
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect((store.getState().requestTaskItem.requestTask.payload as any).aer.monitoringApproachTypes).toEqual(
      remainingMonitoringApproaches,
    );
    expect((store.getState().requestTaskItem.requestTask.payload as any).aerSectionsCompleted).toEqual({
      ...remainingStatusesCompleted,
      monitoringApproachTypes: [false],
    });
  });
});
