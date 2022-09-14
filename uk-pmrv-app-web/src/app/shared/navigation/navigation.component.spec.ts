import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavigationComponent } from './navigation.component';

describe('NavigationComponent', () => {
  @Component({
    template: `<app-navigation [ariaLabel]="ariaLabel"></app-navigation>`,
  })
  class TestComponent {
    ariaLabel = 'Primary navigation';
  }
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NavigationComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default aria label', () => {
    const nav = fixture.nativeElement.querySelector('nav');

    expect(nav.getAttribute('aria-label')).toEqual('Primary navigation');
  });

  it('should set aria label', () => {
    component.ariaLabel = 'Test nav';
    fixture.detectChanges();
    const nav = fixture.nativeElement.querySelector('nav');

    expect(nav.getAttribute('aria-label')).toEqual('Test nav');
  });
});
