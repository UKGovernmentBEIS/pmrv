import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage, expectToHaveNavigatedTo, RouterStubComponent } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ReturnLinkComponent } from './return-link.component';

describe('ListReturnLinkComponent', () => {
  let component: ReturnLinkComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({ template: '<router-outlet></router-outlet>' })
  class TestComponent {}

  @Component({ template: '<app-return-link></app-return-link>' })
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
                ],
              },
            ],
          },
        ]),
      ],
      declarations: [ReturnLinkComponent, TestComponent, RouterStubComponent, ChildComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  describe('for summary routes', () => {
    beforeEach(async () => {
      await TestBed.inject(Router).navigate(['/123/section/summary']);
      fixture = TestBed.createComponent(TestComponent);
      page = new Page(fixture);
      fixture.detectChanges();
      component = fixture.debugElement.query(By.directive(ReturnLinkComponent)).componentInstance;
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
      fixture = TestBed.createComponent(TestComponent);
      page = new Page(fixture);
      fixture.detectChanges();
      component = fixture.debugElement.query(By.directive(ReturnLinkComponent)).componentInstance;
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
