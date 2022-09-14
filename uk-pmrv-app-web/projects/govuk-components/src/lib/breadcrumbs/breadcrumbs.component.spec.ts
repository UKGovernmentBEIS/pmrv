import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { LinkDirective } from '../directives';
import { BreadcrumbsComponent } from './breadcrumbs.component';

describe('BreadcrumbsComponent', () => {
  @Component({
    template: `
      <govuk-breadcrumbs>
        <a govukLink="breadcrumb" href="#">Home</a>
        <a govukLink="breadcrumb" href="#">Travel abroad</a>
        <a govukLink="breadcrumb" href="#">Environment</a>
      </govuk-breadcrumbs>
    `,
  })
  class TestComponent {}

  let component: BreadcrumbsComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [BreadcrumbsComponent, TestComponent, LinkDirective],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(BreadcrumbsComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display breadcrumbs', () => {
    const hostElement: HTMLElement = fixture.nativeElement;

    const items = hostElement.querySelectorAll<HTMLLIElement>('.govuk-breadcrumbs__list-item');
    expect(items.length).toEqual(3);
    expect(items[2].querySelector<HTMLAnchorElement>('.govuk-breadcrumbs__link').textContent).toEqual('Environment');
  });
});
