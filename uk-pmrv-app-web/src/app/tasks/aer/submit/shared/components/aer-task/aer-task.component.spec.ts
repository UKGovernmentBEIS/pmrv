import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerTaskComponent } from './aer-task.component';

describe('AerTaskComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: 'Review follow up response to a notification',
  });

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
      <app-aer-task [breadcrumb]="true">
        <app-page-heading caption="Additional information">Abbreviations and definitions</app-page-heading>
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">
          Uploaded additional documents and information
        </h2>
      </app-aer-task>
    `,
  })
  class TestComponent {
    breadcrumb;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
      declarations: [AerTaskComponent, TestComponent],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  describe('for submit', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      jest.spyOn(store, 'requestTaskType$', 'get').mockReturnValue(of('AER_APPLICATION_SUBMIT'));
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(hostComponent).toBeTruthy();
    });

    it('should display all internal links', () => {
      const links = page.links;

      expect(links).toHaveLength(1);
      expect(links[0].textContent.trim()).toEqual('Change');
    });

    it('should display all internal titles', () => {
      expect(page.pageheadings).toHaveLength(1);
      expect(page.pageheadings[0].textContent.trim()).toEqual('Abbreviations and definitions');

      const pageHeadings = page.headings;

      expect(page.headings).toHaveLength(1);
      expect(pageHeadings[0].textContent.trim()).toEqual('Uploaded additional documents and information  Change');
    });
  });
});
