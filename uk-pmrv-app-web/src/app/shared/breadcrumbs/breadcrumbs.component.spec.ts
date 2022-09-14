import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { BasePage } from '../../../testing';
import { BREADCRUMB_ITEMS } from './breadcrumb.factory';
import { BreadcrumbItem } from './breadcrumb.interface';
import { BreadcrumbsComponent } from './breadcrumbs.component';

describe('BreadcrumbsComponent', () => {
  let component: BreadcrumbsComponent;
  let fixture: ComponentFixture<BreadcrumbsComponent>;
  let page: Page;
  let breadcrumbItem$: BehaviorSubject<BreadcrumbItem[]>;

  class Page extends BasePage<BreadcrumbsComponent> {
    get breadcrumbs() {
      return this.queryAll<HTMLLIElement>('.govuk-breadcrumbs__list-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BreadcrumbsComponent],
      imports: [GovukComponentsModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BreadcrumbsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    breadcrumbItem$ = TestBed.inject(BREADCRUMB_ITEMS);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display breadcrubms', () => {
    expect(page.breadcrumbs).toEqual([]);

    breadcrumbItem$.next([{ text: 'Dashboard', link: ['/dashboard'] }, { text: 'Apply for a permit' }]);
    fixture.detectChanges();

    expect(Array.from(page.breadcrumbs).map((breacrumb) => breacrumb.textContent)).toEqual([
      'Dashboard',
      'Apply for a permit',
    ]);

    expect(page.breadcrumbs[0].querySelector<HTMLAnchorElement>('a').href).toContain('/dashboard');
    expect(page.breadcrumbs[1].querySelector<HTMLAnchorElement>('a')).toBeFalsy();

    breadcrumbItem$.next(null);
    fixture.detectChanges();

    expect(page.breadcrumbs).toEqual([]);
  });
});
