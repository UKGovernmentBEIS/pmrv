import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { IndustrialCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/industrial-crf-code/industrial-crf-code.component';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerRegulatedActivity, TasksService } from 'pmrv-api';

describe('IndustrialCrfCodeComponent', () => {
  let component: IndustrialCrfCodeComponent;
  let fixture: ComponentFixture<IndustrialCrfCodeComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;

  const route = new ActivatedRouteStub({ activityId: '324' });
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<IndustrialCrfCodeComponent> {
    get industrialCrfCategory() {
      return this.fixture.componentInstance.form.get('industrialCrfCategory').value;
    }

    set industrialCrfCategory(value: string) {
      this.fixture.componentInstance.form.get('industrialCrfCategory').setValue(value);
    }

    get industrialCrf() {
      return this.fixture.componentInstance.form.get('industrialCrf').value;
    }

    set industrialCrf(value: AerRegulatedActivity['industrialCrf']) {
      this.fixture.componentInstance.form.get('industrialCrf').setValue(value);
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
    fixture = TestBed.createComponent(IndustrialCrfCodeComponent);
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
      expect(page.title).toEqual('Add an industrial process CRF code');
    });

    it('should raise validation error for industrial crf', () => {
      page.industrialCrfCategory = '_1_A_1';
      page.industrialCrf = null;
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a CRF code']);
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
          hasIndustrialCrf: true,
          energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
          industrialCrf: '_1_A_2_D_PULP_PAPER_AND_PRINT',
        },
      ];
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      page.industrialCrf = '_1_A_2_D_PULP_PAPER_AND_PRINT';
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
      expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
    });
  });
});
