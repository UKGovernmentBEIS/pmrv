import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { buttonClick } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { ConfirmResponsibilityComponent } from './confirm-responsibility.component';

describe('ConfirmResponsibilityComponent', () => {
  let component: ConfirmResponsibilityComponent;
  let fixture: ComponentFixture<ConfirmResponsibilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfirmResponsibilityComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [InstallationAccountApplicationStore],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmResponsibilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate if form valid', inject([Router, ActivatedRoute], (router: Router, route: ActivatedRoute) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const checkboxes = fixture.debugElement.queryAll(By.css('input'));
    checkboxes[0].nativeElement.click();

    buttonClick(fixture);
    expect(navigateSpy).not.toHaveBeenCalled();

    checkboxes[1].nativeElement.click();
    checkboxes[2].nativeElement.click();

    buttonClick(fixture);
    expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: route });
  }));
});
