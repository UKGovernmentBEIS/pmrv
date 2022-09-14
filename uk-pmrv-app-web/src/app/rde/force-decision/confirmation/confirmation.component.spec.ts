import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { RdeModule } from '../../rde.module';
import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let page: Page;
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;

  class Page extends BasePage<ConfirmationComponent> {
    get confirmationMessage() {
      return this.query('.govuk-panel__title').innerHTML.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, RdeModule],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { decision: 'ACCEPTED' } } } as any);
    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show confirmation message', () => {
    expect(page.confirmationMessage).toBe('Extension request approved');
  });
});
