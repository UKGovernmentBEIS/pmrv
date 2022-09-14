import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { PermitTypeSummaryComponent } from './permit-type-summary.component';

describe('PermitTypeSummaryComponent', () => {
  let store: PermitApplicationStore;
  let component: PermitTypeSummaryComponent;
  let fixture: ComponentFixture<PermitTypeSummaryComponent>;

  const createComponent = () => {
    fixture = TestBed.createComponent(PermitTypeSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);

    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
