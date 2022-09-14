import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockDeterminationPostBuild, mockState } from '../../../testing/mock-state';
import { EmissionsComponent } from './emissions.component';

describe('EmissionsComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: EmissionsComponent;
  let fixture: ComponentFixture<EmissionsComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionsComponent> {
    get year() {
      return this.getInputValue('#annualEmissionsTargets.0.year');
    }

    set year(value: string) {
      this.setInputValue('#annualEmissionsTargets.0.year', value);
    }

    get emissions() {
      return this.getInputValue('#annualEmissionsTargets.0.emissions');
    }

    set emissions(value: string) {
      this.setInputValue('#annualEmissionsTargets.0.emissions', value);
    }

    get addTargetBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another target',
      );
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
  }

  const createComponent = () => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(EmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [EmissionsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new emissions targets', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display a form with emprty values', () => {
      expect(page.addTargetBtn).toBeTruthy();
      expect(page.continueButton).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form', () => {
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a year value', 'Enter tonnes CO2e']);

      page.year = '2022';
      page.emissions = '22222';

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockDeterminationPostBuild(
          {
            annualEmissionsTargets: { '2022': 22222 },
          },
          {
            determination: false,
          },
        ),
      );
    });
  });
});
