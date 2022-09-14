import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterNavListComponent } from './nav-list.component';

describe('FooterNavListComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <govuk-footer-nav-list [title]="title" [columns]="columns"></govuk-footer-nav-list> `,
  })
  class TestComponent {
    title = 'Test title';
    columns = 2;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FooterNavListComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display a title', () => {
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    element.querySelector<HTMLHeadingElement>('.govuk-footer__heading');

    expect(element.querySelector<HTMLHeadingElement>('.govuk-footer__heading').textContent).toEqual('Test title');
  });

  it('should contain a two column list', () => {
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    element.querySelector<HTMLHeadingElement>('.govuk-footer__list');

    expect(element.querySelector<HTMLHeadingElement>('.govuk-footer__list').classList).toContain(
      'govuk-footer__list--columns-2',
    );
  });
});
