import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { SubmitComponent } from './submit.component';

describe('Submit Component', () => {
  let component: SubmitComponent;
  let fixture: ComponentFixture<SubmitComponent>;
  let page: Page;

  async function runOnPushChangeDetection(fixture: ComponentFixture<any>): Promise<void> {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  }

  class Page extends BasePage<SubmitComponent> {
    get paragraphInfo() {
      return this.queryAll<HTMLParagraphElement>('p.govuk-body');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SubmitComponent);
    component = fixture.componentInstance;
    component.returnUrlConfig = { text: 'Permit notification', url: '..' };
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  describe('for non filled details', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not allow to submit when apply in progress', () => {
      expect(page.submitButton).toBeNull();
      expect(page.paragraphInfo[0].textContent).toEqual(
        'All tasks must be completed before you can submit your application.',
      );
    });

    it('should  submit when apply is complete', async () => {
      component.competentAuthority = 'ENGLAND';
      component.allowSubmit = true;
      component.isEditable = true;

      const submitted = jest.spyOn(component.submitClicked, 'emit');

      await runOnPushChangeDetection(fixture);
      expect(page.submitButton).toBeTruthy();

      page.submitButton.click();
      expect(submitted).toHaveBeenCalledTimes(1);
    });
  });
});
