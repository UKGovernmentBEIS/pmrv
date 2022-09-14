import { Component } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { delay, of } from 'rxjs';

import { TabsComponent } from '../index';
import { TabDirective } from './tab.directive';

describe('TabDirective', () => {
  describe('with mixed content source, database and static content', () => {
    let component: TabsComponent;
    let fixture: ComponentFixture<TestComponent>;

    @Component({
      template: `
        <govuk-tabs>
          <ng-template govukTab *ngFor="let tab of tabs$ | async" [id]="tab.id" [label]="tab.label">
            {{ tab.body }}
          </ng-template>
          <ng-template govukTab id="paragraph" label="A paragraph">
            <p>This is a paragraph</p>
          </ng-template>
        </govuk-tabs>
      `,
    })
    class TestComponent {
      tabs$ = of([
        { id: 'link', label: 'A link', body: 'This is a link' },
        { id: 'span', label: 'A span', body: 'This is a span' },
      ]).pipe(delay(200));
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [TabsComponent, TestComponent, TabDirective],
        imports: [RouterTestingModule],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(TestComponent);
      component = fixture.debugElement.query(By.directive(TabsComponent)).componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render tabs content asynchronously', fakeAsync(async () => {
      const tabsElement: HTMLElement = fixture.debugElement.query(By.directive(TabsComponent)).nativeElement;
      expect(tabsElement.querySelector('#paragraph').textContent).toContain('This is a paragraph');
    }));

    it('should show only the content of the active tab', async () => {
      await fixture.whenStable();
      fixture.detectChanges();
      const tabsElement: HTMLElement = fixture.debugElement.query(By.directive(TabsComponent)).nativeElement;
      const tabLinks = tabsElement.querySelectorAll<HTMLAnchorElement>('a.govuk-tabs__tab');

      tabLinks[0].click();
      await fixture.whenStable();
      fixture.detectChanges();
      expect(tabsElement.querySelector<HTMLDivElement>('#link')).toBeTruthy();
    });
  });

  describe('with empty data from database', () => {
    let component: TabsComponent;
    let fixture: ComponentFixture<TestComponent>;

    @Component({
      template: `
        <govuk-tabs>
          <ng-template govukTab *ngFor="let tab of tabs$ | async" [id]="tab.id" [label]="tab.label">
            {{ tab.body }}
          </ng-template>
        </govuk-tabs>
      `,
    })
    class TestComponent {
      tabs$ = of([]).pipe(delay(200));
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [TabsComponent, TestComponent, TabDirective],
        imports: [RouterTestingModule],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(TestComponent);
      component = fixture.debugElement.query(By.directive(TabsComponent)).componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not create tab content sections', () => {
      const tabsElement: HTMLElement = fixture.debugElement.query(By.directive(TabsComponent)).nativeElement;
      const tabsContent = tabsElement.querySelectorAll('.govuk-tabs__panel');
      expect(tabsContent.length).toEqual(0);
    });
  });
});
