import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../shared.module';
import { TimelineItemComponent } from './timeline-item.component';

describe('TimelineItemComponent', () => {
  let component: TimelineItemComponent;
  let fixture: ComponentFixture<TimelineItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimelineItemComponent);
    component = fixture.componentInstance;
    component.action = {
      type: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED',
      creationDate: '2020-08-25 10:36:15.189643',
      submitter: 'asd',
    };
    component.link = ['.'];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
