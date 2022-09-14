import { Component, Input, TemplateRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskDTO } from 'pmrv-api';

import { PermitVariationTasklistComponent } from './permit-variation-tasklist.component';

describe('PermitVariationTasklistComponent', () => {
  let component: PermitVariationTasklistComponent;
  let fixture: ComponentFixture<PermitVariationTasklistComponent>;
  let hostElement: HTMLElement;
  let store: CommonTasksStore;

  @Component({
    selector: 'app-base-task-container-component',
    template: `
      <div id="header">{{ header }}</div>
      <ng-container></ng-container>
    `,
  })
  class MockTaskContainerComponent {
    @Input() header: string;
    @Input() expectedTaskType: RequestTaskDTO['type'];
    @Input() customContentTemplate: TemplateRef<any>;
  }

  @Component({
    selector: 'app-permit-task-list',
    template: `permit task list`,
  })
  class MockPermitTaskListComponent {
    @Input() isHeadingVisible: boolean;
    @Input() isPermitVariation: boolean;
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PermitVariationTasklistComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'isEditable$', 'get').mockReturnValue(of(true));
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PermitVariationTasklistComponent, MockPermitTaskListComponent, MockTaskContainerComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    createComponent();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show header', () => {
    expect(component).toBeTruthy();
    expect(hostElement.querySelector('#header').textContent).toEqual('Make a change to your permit');
  });
});
