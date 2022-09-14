import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { CrfCodesComponent } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes.component';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerRegulatedActivity, TasksService } from 'pmrv-api';

describe('CrfCodesComponent', () => {
  let component: CrfCodesComponent;
  let fixture: ComponentFixture<CrfCodesComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;

  const route = new ActivatedRouteStub({ activityId: '324' });
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<CrfCodesComponent> {
    get hasEnergyCrf() {
      return this.fixture.componentInstance.form.get('hasEnergyCrf').value;
    }

    set hasEnergyCrf(value: boolean) {
      this.fixture.componentInstance.form.get('hasEnergyCrf').setValue([value]);
    }

    get hasIndustrialCrf() {
      return this.fixture.componentInstance.form.get('hasIndustrialCrf').value;
    }

    set hasIndustrialCrf(value: boolean) {
      this.fixture.componentInstance.form.get('hasIndustrialCrf').setValue([value]);
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
    fixture = TestBed.createComponent(CrfCodesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding industrial crf', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('CRF code header');
    });

    it('should raise validation error for missing crf', () => {
      page.hasEnergyCrf = false;
      page.hasIndustrialCrf = false;
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select at least one sector']);
    });

    it('should submit a valid form, update the store and navigate', () => {
      expect(page.errorSummary).toBeFalsy();

      const expectedRegulatedActivities: AerRegulatedActivity[] = [
        {
          id: '324',
          type: 'AMMONIA_PRODUCTION',
          capacity: 100,
          capacityUnit: 'KVA',
          hasEnergyCrf: true,
          hasIndustrialCrf: false,
          energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
          industrialCrf: null,
        },
      ];
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      page.hasIndustrialCrf = false;
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
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'energy-crf-code'], { relativeTo: route });
    });
  });
});
