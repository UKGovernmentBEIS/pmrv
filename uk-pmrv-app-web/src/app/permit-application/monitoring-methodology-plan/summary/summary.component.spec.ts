import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { MonitoringMethodologyPlanSummaryComponent } from './summary.component';

describe('MonitoringMethodologyPlanSummaryComponent', () => {
  let store: PermitApplicationStore;
  let component: MonitoringMethodologyPlanSummaryComponent;
  let fixture: ComponentFixture<MonitoringMethodologyPlanSummaryComponent>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringMethodologyPlanSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitoringMethodologyPlanSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            attachments: ['e227ea8a-778b-4208-9545-e108ea66c114'],
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [false],
        },
      ),
    );
  });

  beforeEach(() => {
    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
