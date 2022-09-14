import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewRequestActionState, mockReviewState } from '../../testing/mock-state';
import { ReturnForAmendsGuard } from './return-for-amends.guard';

describe('ReturnForAmendsGuard', () => {
  let guard: ReturnForAmendsGuard;
  let router: Router;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
    });
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(ReturnForAmendsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if action', async () => {
    store.setState({
      ...mockReviewRequestActionState,
    });
    await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(true);

    store.setState({
      ...mockReviewState,
      reviewGroupDecisions: {
        FUELS_AND_EQUIPMENT: {
          type: 'OPERATOR_AMENDS_NEEDED',
          notes: 'Notes',
          changesRequired: 'Changes',
        },
      },
      reviewSectionsCompleted: {
        CONFIDENTIALITY_STATEMENT: true,
        FUELS_AND_EQUIPMENT: true,
        INSTALLATION_DETAILS: true,
        MANAGEMENT_PROCEDURES: true,
        MONITORING_METHODOLOGY_PLAN: true,
        ADDITIONAL_INFORMATION: true,
        DEFINE_MONITORING_APPROACHES: true,
        UNCERTAINTY_ANALYSIS: true,
        CALCULATION: true,
        PFC: true,
        N2O: true,
        INHERENT_CO2: true,
        TRANSFERRED_CO2: true,
        FALLBACK: true,
        MEASUREMENT: true,
      },
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS'],
    });
    await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(true);
  });

  it('should redirect', async () => {
    store.setState({
      ...mockReviewState,
      reviewGroupDecisions: {
        FUELS_AND_EQUIPMENT: {
          type: 'ACCEPTED',
          notes: 'Notes',
          changesRequired: 'Changes',
        },
      },
      reviewSectionsCompleted: {
        CONFIDENTIALITY_STATEMENT: true,
        FUELS_AND_EQUIPMENT: true,
        INSTALLATION_DETAILS: true,
        MANAGEMENT_PROCEDURES: true,
        MONITORING_METHODOLOGY_PLAN: true,
        ADDITIONAL_INFORMATION: true,
        DEFINE_MONITORING_APPROACHES: true,
        UNCERTAINTY_ANALYSIS: true,
        CALCULATION: true,
        PFC: true,
        N2O: true,
        INHERENT_CO2: true,
        TRANSFERRED_CO2: true,
        MEASUREMENT: true,
      },
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS'],
    });
    await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/`),
    );

    store.setState({
      ...mockReviewState,
      reviewGroupDecisions: {
        FUELS_AND_EQUIPMENT: {
          type: 'OPERATOR_AMENDS_NEEDED',
          notes: 'Notes',
          changesRequired: 'Changes',
        },
      },
      reviewSectionsCompleted: {
        CONFIDENTIALITY_STATEMENT: true,
        FUELS_AND_EQUIPMENT: true,
        INSTALLATION_DETAILS: true,
        MANAGEMENT_PROCEDURES: true,
        MONITORING_METHODOLOGY_PLAN: true,
        ADDITIONAL_INFORMATION: true,
        DEFINE_MONITORING_APPROACHES: true,
        UNCERTAINTY_ANALYSIS: true,
        CALCULATION: true,
        PFC: true,
        N2O: true,
        INHERENT_CO2: true,
        TRANSFERRED_CO2: true,
        FALLBACK: true,
        MEASUREMENT: true,
      },
    });
    await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/`),
    );

    store.setState({
      ...mockReviewState,
      reviewGroupDecisions: {
        FUELS_AND_EQUIPMENT: {
          type: 'ACCEPTED',
          notes: 'Notes',
        },
      },
      reviewSectionsCompleted: {
        CONFIDENTIALITY_STATEMENT: true,
        FUELS_AND_EQUIPMENT: true,
        INSTALLATION_DETAILS: true,
        MANAGEMENT_PROCEDURES: true,
        MONITORING_METHODOLOGY_PLAN: true,
        ADDITIONAL_INFORMATION: true,
        DEFINE_MONITORING_APPROACHES: true,
        UNCERTAINTY_ANALYSIS: true,
        CALCULATION: true,
        PFC: true,
        N2O: true,
        INHERENT_CO2: true,
        TRANSFERRED_CO2: true,
        FALLBACK: true,
        MEASUREMENT: true,
      },
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS'],
    });
    await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/`),
    );

    store.setState(mockReviewState);
    await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/`),
    );
  });
});
