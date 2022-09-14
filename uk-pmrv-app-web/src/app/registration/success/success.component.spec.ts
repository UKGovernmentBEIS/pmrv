import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { OperatorUserDTO } from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { SharedModule } from '../../shared/shared.module';
import { initialState } from '../store/user-registration.state';
import { UserRegistrationStore } from '../store/user-registration.store';
import { SuccessComponent } from './success.component';

describe('SuccessComponent', () => {
  let component: SuccessComponent;
  let fixture: ComponentFixture<SuccessComponent>;

  const mockUserOperatorDTO: OperatorUserDTO = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'test@email.com',
    address: {
      line1: 'Line 1',
      city: 'City',
      country: 'Country',
      postcode: 'PostCode',
    },
    phoneNumber: {
      countryCode: 'UK44',
      number: '123',
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SuccessComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [AuthService, KeycloakService, BackLinkService],
    }).compileComponents();

    TestBed.inject(UserRegistrationStore).setState({
      isInvited: true,
      userRegistrationDTO: mockUserOperatorDTO,
      password: 'asdfg',
      token: 'token',
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should reset the store', () => {
    expect(TestBed.inject(UserRegistrationStore).getState()).toEqual(initialState);
  });
});
