import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { delay, of } from 'rxjs';

import { TabsComponent } from '../index';
import { TabLazyDirective } from './tab-lazy.directive';

describe('TabLazyDirective', () => {
  let component: TabsComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: `
      <govuk-tabs>
        <ng-template govukTabLazy *ngFor="let tab of tabsLazy$ | async" [id]="tab.id" [label]="tab.label">
          {{ tab.body }}
        </ng-template>
        <ng-template govukTabLazy id="lazy3" label="Lazy 3"> Lazy 3</ng-template>
      </govuk-tabs>
    `,
  })
  class TestComponent {
    tabsLazy$ = of([
      { id: 'asyncLazy1', label: 'Async Lazy 1', body: 'Async Lazy 1 content' },
      { id: 'asyncLazy2', label: 'Async Lazy 2', body: 'Async Lazy 2 content' },
    ]).pipe(delay(200));
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TabsComponent, TabLazyDirective, TestComponent],
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
});
