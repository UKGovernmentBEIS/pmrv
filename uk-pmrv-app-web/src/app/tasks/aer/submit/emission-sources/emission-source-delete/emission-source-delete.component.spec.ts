import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockPostBuild } from '../../testing/mock-state';
import { EmissionSourceDeleteComponent } from './emission-source-delete.component';

describe('EmissionSourceDeleteComponent', () => {
  let component: EmissionSourceDeleteComponent;
  let fixture: ComponentFixture<EmissionSourceDeleteComponent>;
  let store: CommonTasksStore;
  let router: Router;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ sourceId: mockAerApplyPayload.aer.emissionSources[0].id }, null, {
    permitTask: 'emissionSources',
  });

  class Page extends BasePage<EmissionSourceDeleteComponent> {
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
    fixture = TestBed.createComponent(EmissionSourceDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the emission source name', () => {
    expect(page.header.textContent.trim()).toContain('emission source 1 reference emission source 1 description');
  });

  it('should delete the emission source', () => {
    expect(
      (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
        .emissionSources,
    ).toEqual(mockAerApplyPayload.aer.emissionSources);

    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const targetId = mockAerApplyPayload.aer.emissionSources[0].id;
    const expectedEmissionSources = mockAerApplyPayload.aer.emissionSources.filter((source) => source.id !== targetId);

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild({ emissionSources: expectedEmissionSources }, { emissionSources: [false] }),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(
      (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
        .emissionSources,
    ).toEqual(expectedEmissionSources);
  });
});
