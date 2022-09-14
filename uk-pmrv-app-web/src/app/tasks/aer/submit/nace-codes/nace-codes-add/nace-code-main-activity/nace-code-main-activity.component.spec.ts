import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { NaceCodeMainActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-main-activity/nace-code-main-activity.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('NaceCodeMainActivityComponent', () => {
  let component: NaceCodeMainActivityComponent;
  let fixture: ComponentFixture<NaceCodeMainActivityComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  class Page extends BasePage<NaceCodeMainActivityComponent> {
    get radioButtons(): HTMLUListElement[] {
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
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture = TestBed.createComponent(NaceCodeMainActivityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the content', () => {
    expect(page.heading).toEqual('Select the relevant category for the main activity at the installation');
    expect(page.radioButtons).toHaveLength(21);
  });

  it('should choose selected main activity and navigate to sub category', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.radioButtons[0].click();
    fixture.detectChanges();
    page.submitButton.click();
    expect(navigateSpy).toHaveBeenCalledWith(['sub-category'], {
      relativeTo: route,
      state: { mainActivity: 'AGRICULTURE_FORESTRY_AND_FISHING' },
    });
  });
});
