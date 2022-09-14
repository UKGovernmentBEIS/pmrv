import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { EstimatedEmissionsComponent } from './estimated-emissions.component';

describe('EstimatedEmissionsComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: EstimatedEmissionsComponent;
  let fixture: ComponentFixture<EstimatedEmissionsComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EstimatedEmissionsComponent> {
    get categoryALowEmitterOption() {
      return this.query<HTMLInputElement>('#category-option0');
    }

    get quantity() {
      return this.getInputValue('#quantity');
    }

    set quantity(value: string) {
      this.setInputValue('#quantity', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryText() {
      return this.errorSummary.querySelectorAll('a')[0].textContent.trim();
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EstimatedEmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for a new quantity', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ estimatedAnnualEmissions: undefined }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit at least one category', () => {
      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryText).toEqual('Enter tonnes CO2e');

      page.quantity = '20';
      page.submitButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ estimatedAnnualEmissions: { quantity: 20 } }, { estimatedAnnualEmissions: [true] }),
      );
    });
  });

  describe('for existing installation category', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should fill the form from the store', () => {
      expect(page.quantity).toEqual('33');
    });
  });
});
