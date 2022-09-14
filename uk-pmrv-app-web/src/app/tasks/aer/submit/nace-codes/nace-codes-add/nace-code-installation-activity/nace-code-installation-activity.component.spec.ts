import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { NaceCodeInstallationActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.component';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('NaceCodeInstallationActivityComponent', () => {
  let component: NaceCodeInstallationActivityComponent;
  let fixture: ComponentFixture<NaceCodeInstallationActivityComponent>;
  let page: Page;
  let router: Router;
  let aerService: AerService;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  class Page extends BasePage<NaceCodeInstallationActivityComponent> {
    get radioButtons(): HTMLDivElement[] {
      return Array.from(this.query<HTMLDivElement>('.govuk-radios').querySelectorAll('input[type="radio"]'));
    }

    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, DestroySubject],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    router = TestBed.inject(Router);
    jest
      .spyOn(router, 'getCurrentNavigation')
      .mockReturnValue({ extras: { state: { subCategory: 'FISHING_AND_AQUACULTURE' } } } as any);
    aerService = TestBed.inject(AerService);
    jest.spyOn(aerService, 'getTask').mockReturnValue(of({}));
    route = TestBed.inject(ActivatedRoute);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateBuild());
    fixture = TestBed.createComponent(NaceCodeInstallationActivityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the content', () => {
    expect(page.heading).toEqual("Select the option that describes your installation's activity");
    expect(page.radioButtons).toHaveLength(6);
  });

  it('should submit a valid form and navigate back', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
    postTaskSaveSpy.mockReturnValue(of({}));
    const code = '_311_MARINE_FISHING';

    page.radioButtons[0].click();
    fixture.detectChanges();
    page.radioButtons[1].click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(postTaskSaveSpy).toHaveBeenCalledWith({ naceCodes: { codes: [code] } }, undefined, false, 'naceCodes');
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
