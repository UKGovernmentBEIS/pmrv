import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { SharedModule } from '../../shared/shared.module';
import { TimeoutBannerComponent } from './timeout-banner.component';
import { TimeoutBannerService } from './timeout-banner.service';

describe('TimeoutBannerComponent', () => {
  const originalConsole = console;
  let component: TimeoutBannerComponent;
  let fixture: ComponentFixture<TimeoutBannerComponent>;
  const timeoutBannerService: Partial<jest.Mocked<TimeoutBannerService>> = {
    isVisible$: new BehaviorSubject<boolean>(false),
    extendSession: jest.fn().mockImplementation(),
    signOut: jest.fn().mockImplementation(),
    timeExtensionAllowed$: new BehaviorSubject<boolean>(true),
  };

  beforeEach(async () => {
    console.error = jest.fn();
    console.warn = jest.fn();
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [TimeoutBannerComponent],
      providers: [{ provide: TimeoutBannerService, useValue: timeoutBannerService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeoutBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    console = originalConsole;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open dialog', () => {
    timeoutBannerService.isVisible$.next(true);
    fixture.detectChanges();
    expect(component.isDialogOpen()).toBeTruthy();
  });

  it('should hide dialog', () => {
    timeoutBannerService.isVisible$.next(false);
    fixture.detectChanges();
    expect(component.isDialogOpen()).toBeFalsy();
  });

  it('should extend session', () => {
    const continueBtn = fixture.nativeElement.querySelector('.govuk-button');
    continueBtn.click();

    expect(timeoutBannerService.extendSession).toHaveBeenCalled();
  });

  it('should sign out', () => {
    const continueBtn = fixture.nativeElement.querySelector('.govuk-button--secondary');
    continueBtn.click();

    expect(timeoutBannerService.signOut).toHaveBeenCalled();
  });

  it('should allow user to extend session', () => {
    const textDiv = fixture.nativeElement.querySelector('[aria-relevant="additions"][aria-hidden="true"]');

    expect(textDiv.innerHTML).toContain('if you do not respond');
  });

  it('should not allow user to extend session', () => {
    timeoutBannerService.timeExtensionAllowed$.next(false);
    fixture.detectChanges();
    const textDiv = fixture.nativeElement.querySelector('[aria-relevant="additions"][aria-hidden="true"]');

    expect(textDiv.innerHTML).not.toContain('if you do not respond');
  });
});
