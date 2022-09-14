import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { ErrorMessageComponent } from '../error-message/error-message.component';
import { FileUploadComponent } from './file-upload.component';

describe('FileUploadComponent', () => {
  @Component({
    template: '<div govukFileUpload [formControl]="control"></div>',
  })
  class TestComponent {
    control = new FormControl();
  }

  let component: FileUploadComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [FileUploadComponent, TestComponent, ErrorMessageComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(FileUploadComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable the fileupload', () => {
    hostComponent.control.disable();
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const input = hostElement.querySelector<HTMLInputElement>('input');

    expect(input.disabled).toBeTruthy();
  });

  it('should set as touched on blur', () => {
    const hostElement = fixture.debugElement;
    const input = hostElement.query(By.css('input'));

    expect(hostComponent.control.touched).toBeFalsy();

    input.triggerEventHandler('focus', {});
    input.triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(hostComponent.control.touched).toBeTruthy();
  });
});
