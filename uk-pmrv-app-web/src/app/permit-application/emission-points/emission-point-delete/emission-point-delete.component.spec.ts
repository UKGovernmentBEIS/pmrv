import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { EmissionPointDeleteComponent } from './emission-point-delete.component';

describe('EmissionPointDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: EmissionPointDeleteComponent;
  let fixture: ComponentFixture<EmissionPointDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ emissionPointId: mockState.permit.emissionPoints[0].id }, null, {
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
      imports: [RouterTestingModule, PermitApplicationModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
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
    expect(page.header.textContent).toContain('The big Ref Emission point 1');
  });

  it('should delete the emission point and update dependencies', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const targetId = mockState.permit.emissionPoints[0].id;
    const expectedEmissionPoints = mockState.permit.emissionPoints.filter((point) => point.id !== targetId);
    const expectedEmissionSummaries = mockState.permit.emissionSummaries.map((summary) => ({
      ...summary,
      emissionPoints:
        summary.emissionPoints.length > 1
          ? summary.emissionPoints.filter((summaryPointId) => summaryPointId !== targetId)
          : summary.emissionPoints,
    }));

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        { emissionPoints: expectedEmissionPoints, emissionSummaries: expectedEmissionSummaries },
        { emissionPoints: [false] },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.emissionPoints).toEqual(expectedEmissionPoints);
    expect(store.permit.emissionSummaries).toEqual(expectedEmissionSummaries);
  });
});
