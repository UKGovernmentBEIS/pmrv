import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { ApproachesDeleteComponent } from './approaches-delete.component';

describe('ApproachesDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: ApproachesDeleteComponent;
  let fixture: ComponentFixture<ApproachesDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub(
    {
      monitoringApproach: Object.keys(mockState.permit.monitoringApproaches)[0],
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
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
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

    const storedMonitoringApproaches = mockState.permit.monitoringApproaches;
    const storedStatusesCompleted = mockState.permitSectionsCompleted;
    const deletedMonitoringApproach = activatedRoute.snapshot.paramMap.get('monitoringApproach');

    const remainingMonitoringApproaches = Object.keys(storedMonitoringApproaches)
      .filter((key) => key !== deletedMonitoringApproach)
      .reduce((res, key) => ({ ...res, [key]: storedMonitoringApproaches[key] }), {});

    const remainingStatusesCompleted = Object.keys(storedStatusesCompleted)
      .filter((key) => key !== deletedMonitoringApproach)
      .reduce((res, key) => ({ ...res, [key]: storedStatusesCompleted[key] }), {});

    page.deleteButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
      requestTaskId: 237,
      requestTaskActionPayload: {
        permitSectionsCompleted: {
          ...remainingStatusesCompleted,
          monitoringApproaches: [false],
        },
        payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
        permitType: mockState.permitType,
        permit: {
          ...mockState.permit,
          monitoringApproaches: remainingMonitoringApproaches,
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.monitoringApproaches).toEqual(remainingMonitoringApproaches);
    expect(store.payload.permitSectionsCompleted).toEqual({
      ...remainingStatusesCompleted,
      monitoringApproaches: [false],
    });
  });
});
