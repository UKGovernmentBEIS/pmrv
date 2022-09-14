import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { LinkDirective } from '../../directives';
import { HeaderNavListComponent } from './nav-list.component';

describe('HeaderNavListComponent', () => {
  @Component({
    template: `
      <govuk-header-nav-list
        ariaLabel="Aria label for test navigation"
        identifier="testNavigation"
        menuButtonAriaLabel="Aria label for menu button"
      >
        <a govukLink="header" href="/">Test Link</a>
      </govuk-header-nav-list>
    `,
  })
  class HeaderNavListTestComponent {}

  let component: HeaderNavListComponent;
  let fixture: ComponentFixture<HeaderNavListTestComponent>;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [HeaderNavListComponent, HeaderNavListTestComponent, LinkDirective],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderNavListTestComponent);
    component = fixture.debugElement.query(By.directive(HeaderNavListComponent)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain menu button', () => {
    expect(element.querySelector('button').innerHTML.trim()).toEqual('Menu');
  });

  it('should contain menu button with responsive class', () => {
    expect(element.querySelector('button').className).toEqual('govuk-header__menu-button govuk-js-header-toggle');
  });

  it('should contain an unordered list with govuk classes', () => {
    expect(element.querySelector('ul')).toBeTruthy();
    expect(element.querySelector('ul').className).toEqual('govuk-header__navigation');
  });

  it('should contain an anchor nav link', () => {
    expect(element.querySelector('a')).toBeTruthy();
    expect(element.querySelector('a').innerHTML).toEqual('Test Link');
  });

  it('should show navigation on button click', () => {
    const menuButton = fixture.nativeElement.querySelector('button');
    const navigationList = fixture.nativeElement.querySelector('ul');

    expect(menuButton.getAttribute('aria-expanded')).toEqual('false');

    menuButton.click();
    fixture.detectChanges();

    expect(menuButton.getAttribute('aria-expanded')).toEqual('true');
    expect(menuButton.classList).toContain('govuk-header__menu-button--open');
    expect(navigationList.classList).toContain('govuk-header__navigation--open');
  });
});
