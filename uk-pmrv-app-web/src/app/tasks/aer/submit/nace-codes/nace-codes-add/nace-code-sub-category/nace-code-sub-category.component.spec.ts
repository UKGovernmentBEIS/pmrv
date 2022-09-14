import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { NaceCodeSubCategoryComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('NaceCodeSubCategoryComponent', () => {
  let component: NaceCodeSubCategoryComponent;
  let fixture: ComponentFixture<NaceCodeSubCategoryComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  class Page extends BasePage<NaceCodeSubCategoryComponent> {
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
    route = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    jest
      .spyOn(router, 'getCurrentNavigation')
      .mockReturnValue({ extras: { state: { mainActivity: 'AGRICULTURE_FORESTRY_AND_FISHING' } } } as any);
    fixture = TestBed.createComponent(NaceCodeSubCategoryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the content', () => {
    expect(page.heading).toEqual('Select the relevant sub category');
    expect(page.radioButtons).toHaveLength(3);
  });

  it('should choose selected sub category and navigate to installation activity', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.radioButtons[0].click();
    fixture.detectChanges();
    page.submitButton.click();
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'installation-activity'], {
      relativeTo: route,
      state: { subCategory: 'CROP_AND_ANIMAL_PRODUCTION_HUNTING_AND_RELATED_SERVICE_ACTIVITIES' },
    });
  });
});
