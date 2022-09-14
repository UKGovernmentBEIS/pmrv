import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { EnergyCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/energy-crf-code/energy-crf-code.component';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerRegulatedActivity, TasksService } from 'pmrv-api';

describe('EnergyCrfCodeComponent', () => {
  let component: EnergyCrfCodeComponent;
  let fixture: ComponentFixture<EnergyCrfCodeComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;

  const route = new ActivatedRouteStub({ activityId: '324' });
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<EnergyCrfCodeComponent> {
    get energyCrfCategory() {
      return this.fixture.componentInstance.form.get('energyCrfCategory').value;
    }

    set energyCrfCategory(value: string) {
      this.fixture.componentInstance.form.get('energyCrfCategory').setValue(value);
    }

    get energyCrf() {
      return this.fixture.componentInstance.form.get('energyCrf').value;
    }

    set energyCrf(value: AerRegulatedActivity['energyCrf']) {
      this.fixture.componentInstance.form.get('energyCrf').setValue(value);
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
    fixture = TestBed.createComponent(EnergyCrfCodeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding energy crf', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Add an energy CRF code');
    });

    it('should raise validation error for energy crf', () => {
      page.energyCrfCategory = '_1_A_1';
      page.energyCrf = null;
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
          energyCrf: '_1_A_2_D_PULP_PAPER_AND_PRINT',
          industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
        },
      ];
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      page.energyCrf = '_1_A_2_D_PULP_PAPER_AND_PRINT';
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
      expect(navigateSpy).toHaveBeenCalledWith(['../industrial-crf-code'], { relativeTo: route });
    });
  });
});
