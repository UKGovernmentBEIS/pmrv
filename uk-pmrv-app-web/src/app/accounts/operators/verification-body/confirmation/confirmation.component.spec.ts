import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GovukComponentsModule } from 'govuk-components';

import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule],
      declarations: [ConfirmationComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    component.verificationAccount = 'Test account';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the appointed verification account', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('.govuk-panel__body').textContent).toEqual('Test account');
  });
});
