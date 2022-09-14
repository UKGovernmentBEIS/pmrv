import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { mockTaskState } from '@permit-revocation/testing/mock-state';
import { SharedModule } from '@shared/shared.module';

import { ActivatedRouteStub } from '../../../testing';
import { InvalidDataComponent } from './invalid-data.component';

describe('InvalidDataComponent', () => {
  let component: InvalidDataComponent;
  let fixture: ComponentFixture<InvalidDataComponent>;

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'REVOCATION_NOTIFY',
    pageTitle: 'Invalid data',
    keys: ['effectiveDate'],
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [InvalidDataComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvalidDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
