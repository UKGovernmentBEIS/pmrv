import { Component, NgModule } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router, RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { MarkdownModule } from 'ngx-markdown';

import { GovukComponentsModule } from 'govuk-components';

import { ConvertLinksDirective } from './convert-links.directive';
import { markdownModuleConfig } from './markdown-options';
import { RouterLinkComponent } from './router-link.component';

describe('ConvertLinksDirective', () => {
  let directive: ConvertLinksDirective;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({ template: '<div markdown [data]="data"></div>' })
  class TestComponent {
    data = `
      # Title header
      ## Some message header
      ### Subtitle
      You are currently the Admin user on your account.

      You can add additional Admin users, other Operator users, Consultants or Verifiers.

      Users can be managed at any time from your Dashboard by selecting 'Manage users, contacts or verifiers'

      or by clicking on this link to [Manage users, contacts and verifiers](/dashboard)
    `;
  }

  @NgModule({
    imports: [GovukComponentsModule, RouterModule],
    declarations: [RouterLinkComponent],
    entryComponents: [RouterLinkComponent],
  })
  class TestModule {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConvertLinksDirective, TestComponent],
      imports: [TestModule, RouterTestingModule, MarkdownModule.forRoot(markdownModuleConfig)],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    directive = fixture.debugElement.query(By.directive(ConvertLinksDirective)).componentInstance;

    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should render the text with govuk classes', () => {
    expect(element.querySelectorAll('.govuk-body').length).toEqual(4);
    expect(element.querySelector('.govuk-heading-xl').textContent).toEqual('Title header');
    expect(element.querySelector('.govuk-heading-l').textContent).toEqual('Some message header');
    expect(element.querySelector('.govuk-heading-m').textContent).toEqual('Subtitle');
    expect(element.querySelector('.govuk-link')).toBeTruthy();
  });

  it('should render the links with angular router', () => {
    const link = element.querySelector('a');

    expect(link).toBeTruthy();
    expect(link.href).toContain('/dashboard');
    expect(link.textContent).toEqual(`Manage users, contacts and verifiers`);

    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigateByUrl');
    link.click();

    expect(navigateSpy).toHaveBeenCalled();
  });
});
