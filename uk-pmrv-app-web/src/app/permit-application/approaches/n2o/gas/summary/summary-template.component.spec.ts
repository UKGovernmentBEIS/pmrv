import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitApplicationModule } from '../../../../permit-application.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { SummaryTemplateComponent } from './summary-template.component';

describe('SummaryTemplateComponent', () => {
  let component: SummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitApplicationStore;

  @Component({
    template: ` <app-n2o-approach-gas-summary-template></app-n2o-approach-gas-summary-template> `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedPermitModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild({ ...mockPermitCompletePayload.permit }, { ...mockPermitCompletePayload.permitSectionsCompleted }),
    );
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(SummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
