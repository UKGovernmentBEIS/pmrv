import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { cloneDeep } from 'lodash-es';

import { IdentityBarService } from '../../core/services/identity-bar.service';
import { SharedModule } from '../../shared/shared.module';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { mockState } from '../testing/mock-state';
import { OperatorApplicationComponent } from './operator-application.component';

describe('OperatorApplicationComponent', () => {
  @Component({ template: '<app-operator-application></app-operator-application>' })
  class TestComponent {}

  let component: OperatorApplicationComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: InstallationAccountApplicationStore;
  let barService: IdentityBarService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorApplicationComponent, TestComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [InstallationAccountApplicationStore, IdentityBarService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(InstallationAccountApplicationStore);
    barService = TestBed.inject(IdentityBarService);
    store.setState({ ...cloneDeep(mockState), isReviewed: true });
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(OperatorApplicationComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should give identity bar content', () => {
    expect(barService.content.getValue()).toEqual('<span><b>test, test</b></span>');
  });

  it('should empty identity bar content on destroy', () => {
    fixture.destroy();
    expect(barService.content.getValue()).toBeNull();
  });
});
