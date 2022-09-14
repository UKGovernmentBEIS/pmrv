import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { BasePage, expectToHaveNavigatedTo, mockClass, RouterStubComponent } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { ApproachReturnLinkComponent } from './approach-return-link.component';

describe('ApproachReturnLinkComponent', () => {
  let component: ApproachReturnLinkComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  const tasksService = mockClass(TasksService);

  @Component({ template: '<router-outlet></router-outlet>' })
  class TestComponent {}

  @Component({ template: '<app-approach-return-link reviewGroupUrl="calculation"></app-approach-return-link>' })
  class ChildComponent {}

  class Page extends BasePage<TestComponent> {
    get link() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: ':taskId',
            children: [
              { path: '', component: RouterStubComponent },
              {
                path: 'section',
                children: [
                  { path: '', component: ChildComponent },
                  { path: 'summary', component: ChildComponent },
                  { path: 'answers', component: ChildComponent },
                ],
              },
            ],
          },
        ]),
      ],
      declarations: [ApproachReturnLinkComponent, TestComponent, RouterStubComponent, ChildComponent],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for summary routes', () => {
    beforeEach(async () => {
      await TestBed.inject(Router).navigate(['/123/section/summary']);
      const store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      fixture = TestBed.createComponent(TestComponent);
      page = new Page(fixture);
      fixture.detectChanges();
      component = fixture.debugElement.query(By.directive(ApproachReturnLinkComponent)).componentInstance;
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should navigate to the main list', () => {
      page.link.click();
      fixture.detectChanges();

      expectToHaveNavigatedTo('/123');
    });
  });

  describe('for answers routes', () => {
    beforeEach(async () => {
      await TestBed.inject(Router).navigate(['/123/section/answers']);
      const store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      fixture = TestBed.createComponent(TestComponent);
      page = new Page(fixture);
      fixture.detectChanges();
      component = fixture.debugElement.query(By.directive(ApproachReturnLinkComponent)).componentInstance;
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should navigate to the main list', () => {
      page.link.click();
      fixture.detectChanges();

      expectToHaveNavigatedTo('/123');
    });
  });

  describe('for form routes', () => {
    beforeEach(async () => {
      await TestBed.inject(Router).navigate(['/123/section']);
      const store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      fixture = TestBed.createComponent(TestComponent);
      page = new Page(fixture);
      fixture.detectChanges();
      component = fixture.debugElement.query(By.directive(ApproachReturnLinkComponent)).componentInstance;
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should navigate to the main list', () => {
      page.link.click();
      fixture.detectChanges();

      expectToHaveNavigatedTo('/123');
    });
  });
});
