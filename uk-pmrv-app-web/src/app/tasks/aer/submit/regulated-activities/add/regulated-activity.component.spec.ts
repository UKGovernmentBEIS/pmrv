import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { RegulatedActivityComponent } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.component';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerRegulatedActivity, TasksService } from 'pmrv-api';

describe('RegulatedActivityComponent', () => {
  let component: RegulatedActivityComponent;
  let fixture: ComponentFixture<RegulatedActivityComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;

  const route = new ActivatedRouteStub({ activityId: '324' });
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<RegulatedActivityComponent> {
    get activityCategory() {
      return this.fixture.componentInstance.form.get('activityCategory').value;
    }

    set activityCategory(value: string) {
      this.fixture.componentInstance.form.get('activityCategory').setValue(value);
    }

    get activity() {
      return this.fixture.componentInstance.form.get('activity').value;
    }

    set activity(value: AerRegulatedActivity['type']) {
      this.fixture.componentInstance.form.get('activity').setValue(value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get title() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, AerModule],
      providers: [
        KeycloakService,
        DestroySubject,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(RegulatedActivityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding regulated activity', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Select a regulated activity used at the installation');
    });

    it('should raise validation error for capacity unit', () => {
      page.activity = null;
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select an activity']);
    });

    it('should submit a valid form, update the store and navigate', () => {
      const expectedRegulatedActivities: AerRegulatedActivity[] = [
        {
          id: '324',
          type: 'COKE_PRODUCTION',
          capacity: 100,
          capacityUnit: 'KVA',
          hasEnergyCrf: true,
          hasIndustrialCrf: true,
          energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
          industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
        },
      ];
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      page.activity = null;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select an activity']);

      page.activityCategory = 'METAL_GROUP';
      fixture.detectChanges();
      page.activity = 'COKE_PRODUCTION';
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(postTaskSaveSpy).toHaveBeenCalledWith(
        { regulatedActivities: expectedRegulatedActivities },
        {},
        false,
        'regulatedActivities',
      );
      expect(navigateSpy).toHaveBeenCalledWith(['..', '324', 'capacity'], { relativeTo: route });
    });
  });
});
