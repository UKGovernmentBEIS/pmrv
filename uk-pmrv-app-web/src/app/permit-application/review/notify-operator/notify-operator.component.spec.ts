import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { NotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let route: ActivatedRouteStub;
  let store: PermitApplicationStore;
  let component: NotifyOperatorComponent;
  let fixture: ComponentFixture<NotifyOperatorComponent>;

  beforeEach(async () => {
    route = new ActivatedRouteStub({ taskId: '237', index: '0' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [NotifyOperatorComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(NotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
