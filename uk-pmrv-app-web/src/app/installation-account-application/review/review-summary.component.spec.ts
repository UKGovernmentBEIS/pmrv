import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../shared/shared.module';
import { ReviewSummaryComponent } from './review-summary.component';

describe('ReviewSummaryComponent', () => {
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({ template: `<app-review-summary [item]="item" [taskId]="taskId"></app-review-summary>` })
  class TestComponent {
    item = {
      legalEntity: { type: 'LIMITED_COMPANY' },
      location: { type: 'ONSHORE' },
      competentAuthority: 'ENGLAND',
    };
    taskId = 321;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, ReviewSummaryComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });
});
