import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { PeerReviewDecisionComponent } from './peer-review-decision.component';

describe('PeerReviewDecisionComponent', () => {
  let component: PeerReviewDecisionComponent;
  let fixture: ComponentFixture<PeerReviewDecisionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PeerReviewDecisionComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeerReviewDecisionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
