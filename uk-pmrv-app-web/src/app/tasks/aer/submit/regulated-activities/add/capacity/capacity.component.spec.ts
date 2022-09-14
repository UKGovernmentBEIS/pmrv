import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { CapacityComponent } from '@tasks/aer/submit/regulated-activities/add/capacity/capacity.component';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerRegulatedActivity, TasksService } from 'pmrv-api';

describe('CapacityComponent', () => {
  let component: CapacityComponent;
  let fixture: ComponentFixture<CapacityComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;

  const route = new ActivatedRouteStub({ activityId: '324' });
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<CapacityComponent> {
    get capacity() {
      return this.getInputValue('#activityCapacity');
    }

    set capacity(value: number) {
      this.setInputValue('#activityCapacity', value);
    }

    get capacityUnit() {
      return this.getInputValue('#activityCapacityUnit');
    }

    set capacityUnit(value: AerRegulatedActivity['capacityUnit']) {
      this.setInputValue('#activityCapacityUnit', value);
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
    fixture = TestBed.createComponent(CapacityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding capacity', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('What is the total capacity for this regulated activity?');
    });

    it('should raise validation error for capacity unit', () => {
      page.capacity = null;
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter total capacity']);
    });

    it('should submit a valid form, update the store and navigate', () => {
      const expectedRegulatedActivities: AerRegulatedActivity[] = [
        {
          id: '324',
          type: 'AMMONIA_PRODUCTION',
          capacity: 200,
          capacityUnit: 'KG_PER_DAY',
          hasEnergyCrf: true,
          hasIndustrialCrf: true,
          energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
          industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
        },
      ];
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      page.capacity = null;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter total capacity']);

      page.capacity = 200;
      page.capacityUnit = 'KG_PER_DAY';
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
      expect(navigateSpy).toHaveBeenCalledWith(['../crf-codes'], { relativeTo: route });
    });
  });
});
