import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { CommencementComponent } from './commencement.component';

describe('CommencementComponent', () => {
  let component: CommencementComponent;
  let fixture: ComponentFixture<CommencementComponent>;
  let page: Page;

  class Page extends BasePage<CommencementComponent> {
    set commencementDateDay(value: string) {
      this.setInputValue('#commencementDate-day', value);
    }

    set commencementDateMonth(value: string) {
      this.setInputValue('#commencementDate-month', value);
    }

    set commencementDateYear(value: string) {
      this.setInputValue('#commencementDate-year', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommencementComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [InstallationAccountApplicationStore],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommencementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate only if form valid', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.commencementDateDay = '10';
    page.commencementDateMonth = '10';
    fixture.detectChanges();

    page.submitButton.click();

    expect(navigateSpy).not.toHaveBeenCalled();

    page.commencementDateYear = '2020';
    fixture.detectChanges();

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalled();
  }));
});
