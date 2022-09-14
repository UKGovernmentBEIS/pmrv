import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { buttonClick, changeInputValue } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { installationFormFactory } from '../factories/installation-form.factory';
import { InstallationTypeComponent } from './installation-type.component';

describe('InstallationTypeComponent', () => {
  let component: InstallationTypeComponent;
  let fixture: ComponentFixture<InstallationTypeComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InstallationTypeComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [installationFormFactory],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InstallationTypeComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not navigate on no selection', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should navigate on selection', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    changeInputValue(fixture, '#type-option0', true);
    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalled();
  });
});
