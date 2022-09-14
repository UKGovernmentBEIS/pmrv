import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { ReturnForAmendsComponent } from './return-for-amends.component';

describe('ReturnForAmendsComponent', () => {
  let store: PermitApplicationStore;
  let page: Page;
  let component: ReturnForAmendsComponent;
  let fixture: ComponentFixture<ReturnForAmendsComponent>;
  let route: ActivatedRouteStub;
  let router: Router;
  let tasksService: MockType<TasksService>;

  class Page extends BasePage<ReturnForAmendsComponent> {
    get summary() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    tasksService = {
      processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of({})),
    };
    route = new ActivatedRouteStub({ taskId: '237' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitApplicationModule],
      declarations: [ReturnForAmendsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    await TestBed.configureTestingModule({
      declarations: [ReturnForAmendsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      reviewGroupDecisions: {
        CONFIDENTIALITY_STATEMENT: {
          type: 'OPERATOR_AMENDS_NEEDED',
          changesRequired: 'Changes for operator',
          notes: 'notes',
        },
        INSTALLATION_DETAILS: {
          type: 'ACCEPTED',
          notes: 'notes',
        },
      },
    });
    fixture = TestBed.createComponent(ReturnForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the amends, submit and navigate to confirmation', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.summary).toEqual([['Changes required', 'Changes for operator']]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalled();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['./confirmation'], { relativeTo: route });
  });
});
