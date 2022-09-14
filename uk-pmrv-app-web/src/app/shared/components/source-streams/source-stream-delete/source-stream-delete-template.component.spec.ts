import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SourceStreamDeleteTemplateComponent } from '@shared/components/source-streams/source-stream-delete/source-stream-delete-template.component';
import { SharedModule } from '@shared/shared.module';

import { SourceStream } from 'pmrv-api';

describe('SourceStreamDeleteTemplateComponent', () => {
  let component: SourceStreamDeleteTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-source-stream-delete-template
        (delete)="onDelete()"
        [sourceStream]="sourceStream"
      ></app-source-stream-delete-template>
    `,
  })
  class TestComponent {
    sourceStream = {
      id: '124',
      description: 'ANTHRACITE',
      reference: 'reference',
      type: 'AMMONIA_FUEL_AS_PROCESS_INPUT',
    } as SourceStream;
    onDelete: () => any | jest.SpyInstance<void>;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SourceStreamDeleteTemplateComponent)).componentInstance;
    hostComponent.onDelete = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the remove button and stream reference', () => {
    expect(element.querySelectorAll<HTMLButtonElement>('button[govukwarnbutton]').length).toEqual(1);
    expect(element.querySelector('h1').textContent.trim()).toEqual(
      'Are you sure you want to delete  ‘reference Anthracite’?',
    );
  });
});
