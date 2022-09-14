import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { SummaryContainerComponent } from '@permit-revocation/permit-revocation-apply/summary';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';

describe('SummaryContainerComponent', () => {
  let component: SummaryContainerComponent;
  let fixture: ComponentFixture<SummaryContainerComponent>;
  let store: PermitRevocationStore;
  let router: Router;
  let page: Page;

  class Page extends BasePage<SummaryContainerComponent> {
    get notificationBanner() {
      return this.query<HTMLElement>('.govuk-notification-banner.govuk-notification-banner--success');
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'REVOCATION_APPLY',
    keys: ['feeDate', 'effectiveDate'],
    skipValidators: true,
  });

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
    store = TestBed.inject(PermitRevocationStore);
    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      permitRevocation: {
        reason: 'Because i have to',
        activitiesStopped: true,
        stoppedDate: '2022-04-14',
        effectiveDate: '2022-05-16',
        surrenderRequired: false,
        annualEmissionsReportRequired: true,
        annualEmissionsReportDate: '2022-04-14',
        feeCharged: false,
      },
      sectionsCompleted: { REVOCATION_APPLY: true },
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display Notification banner', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    createComponent();
    fixture.detectChanges();
    expect(page.notificationBanner).not.toBeNull();
  });
});
