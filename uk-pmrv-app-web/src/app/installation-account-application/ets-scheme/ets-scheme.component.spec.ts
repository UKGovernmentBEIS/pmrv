import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { installationFormFactory } from '../factories/installation-form.factory';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { EtsSchemeComponent } from './ets-scheme.component';

describe('EtsSchemeComponent', () => {
  let component: EtsSchemeComponent;
  let router: Router;
  let store: InstallationAccountApplicationStore;
  let fixture: ComponentFixture<EtsSchemeComponent>;
  let page: Page;

  class Page extends BasePage<EtsSchemeComponent> {
    get etsSchemeTypes() {
      return this.queryAll<HTMLInputElement>('input[name="etsSchemeType"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [EtsSchemeComponent],
      providers: [InstallationAccountApplicationStore, installationFormFactory],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EtsSchemeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    store = TestBed.inject(InstallationAccountApplicationStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not save value in store if form not valid', () => {
    const setStateSpy = jest.spyOn(store, 'setState');
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.submitButton.click();

    expect(setStateSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should fill form from state', () => {
    component.form.patchValue({ etsSchemeType: 'UK_ETS_INSTALLATIONS' });
    fixture.detectChanges();

    expect(page.etsSchemeTypes[0].checked).toBeTruthy();
  });

  it('should save value in store', () => {
    page.etsSchemeTypes[1].click();
    fixture.detectChanges();
    page.submitButton.click();

    expect(component.form.get('etsSchemeType').value).toEqual('EU_ETS_INSTALLATIONS');
  });
});
