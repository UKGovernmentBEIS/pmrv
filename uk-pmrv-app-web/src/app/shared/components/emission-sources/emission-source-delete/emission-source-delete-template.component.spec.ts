import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionSourceDeleteTemplateComponent } from '@shared/components/emission-sources/emission-source-delete/emission-source-delete-template.component';
import { SharedModule } from '@shared/shared.module';

import { EmissionSource } from 'pmrv-api';

describe('EmissionSourcesDeleteTemplateComponent', () => {
  let component: EmissionSourceDeleteTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-emission-source-delete-template
        (delete)="onDelete()"
        [emissionSource]="emissionSource"
      ></app-emission-source-delete-template>
    `,
  })
  class TestComponent {
    emissionSource = {
      id: '124',
      description: 'description',
      reference: 'reference',
    } as EmissionSource;
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
    component = fixture.debugElement.query(By.directive(EmissionSourceDeleteTemplateComponent)).componentInstance;
    hostComponent.onDelete = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the remove button and source reference', () => {
    expect(element.querySelectorAll<HTMLButtonElement>('button[govukwarnbutton]').length).toEqual(1);
    expect(element.querySelector('h1').textContent.trim()).toEqual(
      'Are you sure you want to delete  ‘reference description’?',
    );
  });
});
