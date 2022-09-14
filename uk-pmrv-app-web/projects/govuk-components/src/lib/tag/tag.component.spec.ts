import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagComponent } from './tag.component';

describe('TagComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <govuk-tag [color]="color"></govuk-tag> `,
  })
  class TestComponent {
    color = 'green';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TagComponent, TestComponent],
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

  it('should be in green color', () => {
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    element.querySelector<HTMLElement>('.govuk-tag');

    expect(element.querySelector<HTMLElement>('.govuk-tag').classList).toContain('govuk-tag--green');
  });
});
