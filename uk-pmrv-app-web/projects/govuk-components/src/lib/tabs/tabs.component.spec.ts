import { Component } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { delay, of } from 'rxjs';

import { TabDirective } from './tab/tab.directive';
import { TabsComponent } from './tabs.component';

describe('TabsComponent', () => {
  let component: TabsComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let router: Router;

  @Component({
    template: `
      <govuk-tabs>
        <ng-template govukTab *ngFor="let tab of tabs$ | async" [id]="tab.id" [label]="tab.label">
          {{ tab.body }}
        </ng-template>
        <ng-template govukTab id="paragraph2" label="Another paragraph">
          <p>This is another paragraph</p>
        </ng-template>
        <ng-template govukTab [id]="syncTab.id" [label]="syncTab.label"></ng-template>
      </govuk-tabs>
    `,
  })
  class TestComponent {
    tabs$ = of([
      { id: 'paragraph', label: 'A paragraph', body: 'This is a paragraph' },
      { id: 'link', label: 'A link', body: 'This is a link' },
      { id: 'span', label: 'A span', body: 'This is a span' },
    ]).pipe(delay(200));

    syncTab = { id: 'paragraph3', label: 'A static link', body: 'This is a static link' };
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TabsComponent, TestComponent, TabDirective],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(TabsComponent)).componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create the tab container', () => {
    const tabs = fixture.nativeElement.querySelector('.govuk-tabs');
    expect(tabs).toBeTruthy();
  });

  it('should render async tabs', fakeAsync(async () => {
    const tabsElement: HTMLElement = fixture.debugElement.query(By.directive(TabsComponent)).nativeElement;

    let anchors = tabsElement.querySelectorAll<HTMLAnchorElement>('a.govuk-tabs__tab');
    expect(anchors.length).toEqual(2);

    await fixture.whenStable();
    fixture.detectChanges();

    anchors = tabsElement.querySelectorAll<HTMLAnchorElement>('a.govuk-tabs__tab');
    expect(anchors.length).toEqual(5);

    const anchor1 = tabsElement.querySelector<HTMLAnchorElement>('#tab_paragraph');
    expect(anchor1.textContent).toContain('A paragraph');

    const anchor2 = tabsElement.querySelector<HTMLAnchorElement>('#tab_link');
    expect(anchor2.textContent).toContain('A link');

    const anchor3 = tabsElement.querySelector<HTMLAnchorElement>('#tab_span');
    expect(anchor3.textContent).toContain('A span');
  }));

  it('should set a clicked anchor as the active one', async () => {
    const tabsElement: HTMLElement = fixture.debugElement.query(By.directive(TabsComponent)).nativeElement;
    let anchors = tabsElement.querySelectorAll<HTMLAnchorElement>('a.govuk-tabs__tab');

    expect(anchors[0].parentElement.classList).toContain('govuk-tabs__list-item--selected');

    await fixture.whenStable();
    fixture.detectChanges();

    anchors = tabsElement.querySelectorAll<HTMLAnchorElement>('a.govuk-tabs__tab');

    expect(anchors[0].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[1].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[2].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[3].parentElement.classList).toContain('govuk-tabs__list-item--selected');

    anchors[1].click();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(anchors[0].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[1].parentElement.classList).toContain('govuk-tabs__list-item--selected');
    expect(anchors[2].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[3].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');

    anchors[3].click();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(anchors[0].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[1].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[2].parentElement.classList).not.toContain('govuk-tabs__list-item--selected');
    expect(anchors[3].parentElement.classList).toContain('govuk-tabs__list-item--selected');
  });

  it('should navigate with arrows', fakeAsync(async () => {
    const tabsElement: HTMLElement = fixture.debugElement.query(By.directive(TabsComponent)).nativeElement;
    await fixture.whenStable();
    fixture.detectChanges();

    const anchors = tabsElement.querySelectorAll<HTMLAnchorElement>('a.govuk-tabs__tab');
    anchors[0].focus();
    const linkTab = tabsElement.querySelectorAll<HTMLDivElement>('#link');

    expect(router.url).toEqual('/');
    expect(anchors[0]).toEqual(document.activeElement);
    expect(anchors[1]).not.toEqual(document.activeElement);
    expect(linkTab).toBeTruthy();

    anchors[0].dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight' }));
    await fixture.whenStable();

    expect(router.url).toEqual('/#link');
    expect(anchors[0]).not.toEqual(document.activeElement);
    expect(anchors[1]).toEqual(document.activeElement);

    anchors[1].dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowLeft' }));
    await fixture.whenStable();

    expect(router.url).toEqual('/#paragraph');
    expect(anchors[0]).toEqual(document.activeElement);
    expect(anchors[1]).not.toEqual(document.activeElement);
  }));

  it('should change the tab label', () => {
    const element: HTMLElement = fixture.nativeElement;
    const getAnchorTexts = () => Array.from(element.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    expect(getAnchorTexts()).toContain('A static link');

    hostComponent.syncTab.label = 'Another static link';

    fixture.detectChanges();

    expect(getAnchorTexts()).toContain('Another static link');
  });
});
