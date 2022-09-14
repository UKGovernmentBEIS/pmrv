import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { ActivatedRouteStub, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../shared.module';
import { PeerReviewSubmittedComponent } from './peer-review-submitted.component';

describe('PeerReviewSubmittedComponent', () => {
  let hostComponent: PeerReviewSubmittedComponent;
  let fixture: ComponentFixture<PeerReviewSubmittedComponent>;

  const requestActionsService = mockClass(RequestActionsService);
  requestActionsService.getRequestActionByIdUsingGET.mockReturnValue(
    of({
      creationDate: '2020-08-25 10:36:15.189643',
      payload: {
        decision: {
          type: 'AGREE',
          notes: 'I strongly agree',
        },
      },
      submitter: 'John Bolt',
    } as any),
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ actionId: 1 }) },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeerReviewSubmittedComponent);
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });
});
