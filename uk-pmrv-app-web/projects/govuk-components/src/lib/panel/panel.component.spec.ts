import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { PanelComponent } from './panel.component';

describe('PanelComponent', () => {
  let component: PanelComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <govuk-panel [title]="title"></govuk-panel> `,
  })
  class TestComponent {
    title = 'Something is going on';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PanelComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(PanelComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display a title', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector<HTMLHeadingElement>('.govuk-panel__title').textContent).toEqual(testComponent.title);
  });
});
