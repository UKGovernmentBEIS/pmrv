import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { EmissionSummaryDeleteComponent } from './emission-summary-delete.component';

describe('EmissionSummaryDeleteComponent', () => {
  let component: EmissionSummaryDeleteComponent;
  let fixture: ComponentFixture<EmissionSummaryDeleteComponent>;
  let page: Page;
  let store: PermitApplicationStore;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ emissionSummaryIndex: 0 });

  class Page extends BasePage<EmissionSummaryDeleteComponent> {
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EmissionSummaryDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete the emission summary', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild({ emissionSummaries: [] }, { emissionSummaries: [false] }),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.emissionSummaries).toEqual([]);
  });
});
