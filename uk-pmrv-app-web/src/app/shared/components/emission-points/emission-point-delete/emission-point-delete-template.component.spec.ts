import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionPointDeleteTemplateComponent } from '@shared/components/emission-points/emission-point-delete/emission-point-delete-template.component';
import { SharedModule } from '@shared/shared.module';

import { EmissionPoint } from 'pmrv-api';

describe('EmissionPointDeleteTemplateComponent', () => {
  let component: EmissionPointDeleteTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-emission-point-delete-template
        (delete)="onDelete()"
        [emissionPoint]="emissionPoint"
      ></app-emission-point-delete-template>
    `,
  })
  class TestComponent {
    emissionPoint = {
      id: '258',
      reference: 'EP1',
      description: 'boiler',
    } as EmissionPoint;
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
    component = fixture.debugElement.query(By.directive(EmissionPointDeleteTemplateComponent)).componentInstance;
    hostComponent.onDelete = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the remove button and the emission point to be deleted', () => {
    expect(element.querySelectorAll<HTMLButtonElement>('button[govukwarnbutton]').length).toEqual(1);
    expect(element.querySelector('h1').textContent.trim()).toEqual('Are you sure you want to delete  ‘EP1 boiler’?');
  });
});
