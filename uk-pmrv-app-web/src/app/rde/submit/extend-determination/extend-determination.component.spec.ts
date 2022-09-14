import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { GovukDatePipe } from '../../../shared/pipes/govuk-date.pipe';
import { SharedModule } from '../../../shared/shared.module';
import { RdeModule } from '../../rde.module';
import { RdeStore } from '../../store/rde.store';
import { ExtendDeterminationComponent } from './extend-determination.component';

describe('ExtendDeterminationComponent', () => {
  let component: ExtendDeterminationComponent;
  let fixture: ComponentFixture<ExtendDeterminationComponent>;
  let page: Page;
  let store: RdeStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, RdeModule],
      providers: [GovukDatePipe],
    }).compileComponents();
  });

  class Page extends BasePage<ExtendDeterminationComponent> {
    set extensionDateDay(value: string) {
      this.setInputValue('#extensionDate-day', value);
    }

    set extensionDateMonth(value: string) {
      this.setInputValue('#extensionDate-month', value);
    }

    set extensionDateYear(value: string) {
      this.setInputValue('#extensionDate-year', value);
    }

    set deadlineDay(value: string) {
      this.setInputValue('#deadline-day', value);
    }

    set deadlineMonth(value: string) {
      this.setInputValue('#deadline-month', value);
    }

    set deadlineYear(value: string) {
      this.setInputValue('#deadline-year', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    store = TestBed.inject(RdeStore);
    store.setState({
      ...store.getState(),
      requestTaskId: 1,
      daysRemaining: 10,
    });

    fixture = TestBed.createComponent(ExtendDeterminationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate only if form valid', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.extensionDateDay = '10';
    page.extensionDateMonth = '10';
    fixture.detectChanges();

    page.submitButton.click();

    expect(navigateSpy).not.toHaveBeenCalled();

    page.extensionDateYear = '2026';

    page.deadlineDay = '08';
    page.deadlineMonth = '10';
    page.deadlineYear = '2026';
    fixture.detectChanges();

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalled();
  }));
});
