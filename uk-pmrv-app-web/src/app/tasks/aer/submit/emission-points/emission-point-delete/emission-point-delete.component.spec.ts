import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { EmissionPointDeleteComponent } from '@tasks/aer/submit/emission-points/emission-point-delete/emission-point-delete.component';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockPostBuild } from '../../testing/mock-state';

describe('EmissionPointDeleteComponent', () => {
  let component: EmissionPointDeleteComponent;
  let fixture: ComponentFixture<EmissionPointDeleteComponent>;
  let store: CommonTasksStore;
  let router: Router;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ emissionPointId: mockAerApplyPayload.aer.emissionPoints[0].id }, null, {
    permitTask: 'emissionPoints',
  });

  class Page extends BasePage<EmissionPointDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EmissionPointDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the emission point name', () => {
    expect(page.header.textContent.trim()).toContain('EP1 west side chimney');
  });

  it('should delete the emission point', () => {
    expect(
      (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
        .emissionPoints,
    ).toEqual(mockAerApplyPayload.aer.emissionPoints);

    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const targetId = mockAerApplyPayload.aer.emissionPoints[0].id;
    const expectedEmissionPoints = mockAerApplyPayload.aer.emissionPoints.filter((ep) => ep.id !== targetId);

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild({ emissionPoints: expectedEmissionPoints }, { emissionPoints: [false] }),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(
      (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
        .emissionPoints,
    ).toEqual(expectedEmissionPoints);
  });
});