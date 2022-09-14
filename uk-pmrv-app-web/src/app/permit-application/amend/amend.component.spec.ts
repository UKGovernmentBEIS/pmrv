import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { AmendComponent } from './amend.component';

describe('AmendComponent', () => {
  let component: AmendComponent;
  let fixture: ComponentFixture<AmendComponent>;
  let store: PermitApplicationStore;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', section: 'fuels' });

  class Page extends BasePage<AmendComponent> {
    get amendsNeedList() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row');
    }

    get confirmCheckbox() {
      return this.query<HTMLInputElement>('input#changes-0');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(async () => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      reviewGroupDecisions: {
        FUELS_AND_EQUIPMENT: {
          type: 'OPERATOR_AMENDS_NEEDED',
          changesRequired: 'Changes required',
          notes: 'notes',
        },
      },
    });
    fixture = TestBed.createComponent(AmendComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should successfully submit task and navigate', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    expect(page.confirmCheckbox.checked).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual([
      'Check the box to confirm you have made changes and want to mark as complete',
    ]);

    page.confirmCheckbox.click();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.confirmCheckbox.checked).toBeTruthy();
    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild({}, { AMEND_fuels: [true] }),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../../amend/fuels/summary'], { relativeTo: route });
  });
});
