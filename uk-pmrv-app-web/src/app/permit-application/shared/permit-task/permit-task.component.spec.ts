import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { UserStatusDTO } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { ListReturnLinkComponent } from '../list-return-link/list-return-link.component';
import { PermitTaskComponent } from './permit-task.component';

describe('PermitTaskComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  class Page extends BasePage<TestComponent> {
    get links(): HTMLAnchorElement[] {
      return this.queryAll<HTMLAnchorElement>('a');
    }
    get pageheadings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h1');
    }
    get headings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
  }

  @Component({
    template: `
      <app-permit-task>
        <app-page-heading caption="Additional information">Additional documents and information</app-page-heading>
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">
          Uploaded additional documents and information
        </h2>
        <app-list-return-link></app-list-return-link>
      </app-permit-task>
    `,
  })
  class TestComponent {
    breadcrumb;
  }

  const setStateAndInitiateFixture = (roleType: UserStatusDTO['roleType']) => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState({ ...store.getState(), userViewRole: roleType });

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [PermitTaskComponent, TestComponent, ListReturnLinkComponent],
    }).compileComponents();
  });

  describe('for operator', () => {
    beforeEach(() => {
      setStateAndInitiateFixture('OPERATOR');
    });

    it('should create', () => {
      expect(hostComponent).toBeTruthy();
    });

    it('should display all internal links', () => {
      const links = page.links;

      expect(links).toHaveLength(2);
      expect(links[0].textContent.trim()).toEqual('Change');
      expect(links[1].textContent.trim()).toEqual('Return to: Apply for a permit');
    });

    it('should display all internal titles', () => {
      expect(page.pageheadings).toHaveLength(1);
      expect(page.pageheadings[0].textContent.trim()).toEqual('Additional documents and information');

      const pageHeadings = page.headings;

      expect(page.headings).toHaveLength(1);
      expect(pageHeadings[0].textContent.trim()).toEqual('Uploaded additional documents and information  Change');
    });
  });

  describe('for regulator', () => {
    beforeEach(() => {
      setStateAndInitiateFixture('REGULATOR');
    });

    it('should create', () => {
      expect(hostComponent).toBeTruthy();
    });

    it('should display all internal links', () => {
      const links = page.links;

      expect(links).toHaveLength(2);
      expect(links[0].textContent.trim()).toEqual('Change');
      expect(links[1].textContent.trim()).toEqual('Return to: Apply for a permit');
    });

    it('should display all internal titles', () => {
      expect(page.pageheadings).toHaveLength(1);
      expect(page.pageheadings[0].textContent.trim()).toEqual('Additional documents and information');

      const pageHeadings = page.headings;

      expect(page.headings).toHaveLength(1);
      expect(pageHeadings[0].textContent.trim()).toEqual('Uploaded additional documents and information  Change');
    });
  });
});
