import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { RdeModule } from '../../rde.module';
import { RdeStore } from '../../store/rde.store';
import { TimelineComponent } from './timeline.component';

describe('TimelineComponent', () => {
  let component: TimelineComponent;
  let fixture: ComponentFixture<TimelineComponent>;

  let store: RdeStore;

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, RdeModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteStub }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RdeStore);
    store.setState({
      ...store.getState(),
      rdePayload: {
        ...store.getState().rdePayload,
        operators: ['user1'],
        signatory: 'user2',
      },
      usersInfo: {
        user1: {
          name: 'User1',
          roleCode: 'operator',
          contactTypes: ['PRIMARY'],
        },
        user2: {
          name: 'User2',
        },
      },
    });

    fixture = TestBed.createComponent(TimelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
