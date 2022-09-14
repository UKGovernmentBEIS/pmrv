import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
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
      imports: [SharedModule, RouterTestingModule],
      declarations: [ConfirmationComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show granted message', () => {
    expect(page.confirmationMessage).toBe('Returned to operator for amends');
  });
});
