import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { buttonClick, changeInputValue } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { installationFormFactory } from '../factories/installation-form.factory';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { GasEmissionsDetailsComponent } from './gas-emissions-details.component';

describe('GasEmissionsDetailsComponent', () => {
  let component: GasEmissionsDetailsComponent;
  let fixture: ComponentFixture<GasEmissionsDetailsComponent>;
  let store: InstallationAccountApplicationStore;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GasEmissionsDetailsComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [InstallationAccountApplicationStore, installationFormFactory],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GasEmissionsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    store = TestBed.inject(InstallationAccountApplicationStore);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not save value in store if form not valid', () => {
    const setStateSpy = jest.spyOn(store, 'setState');
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    buttonClick(fixture);

    expect(setStateSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should fill form from state', () => {
    component.form.patchValue({ location: 'ENGLAND' });
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css(`#location-option0`)).properties.checked).toBeTruthy();
  });

  it('should save value in store', () => {
    changeInputValue(fixture, '#location-option1');
    buttonClick(fixture);

    expect(component.form.get('location').value).toEqual('WALES');
  });
});
