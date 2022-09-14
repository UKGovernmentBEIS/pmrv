import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import {
  OperatorUserDTO,
  OperatorUserRegistrationWithCredentialsDTO,
  OperatorUsersRegistrationService,
} from 'pmrv-api';

import { buttonClick } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { UserRegistrationStore } from '../store/user-registration.store';
import { SummaryComponent } from './summary.component';

const mockUserRegistrationDTO: OperatorUserRegistrationWithCredentialsDTO = {
  firstName: 'John',
  lastName: 'Doe',
  emailToken: 'test@email.com',
  password: 'test',
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

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: UserRegistrationStore;
  let router: Router;
  let service: OperatorUsersRegistrationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [UserRegistrationStore],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(UserRegistrationStore);
    router = TestBed.inject(Router);
    service = TestBed.inject(OperatorUsersRegistrationService);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a new user with given data', () => {
    jest
      .spyOn(store, 'select')
      .mockReturnValueOnce(of(mockUserRegistrationDTO))
      .mockReturnValueOnce(of('password'))
      .mockReturnValueOnce(of('token'));
    jest.spyOn(service, 'registerUserUsingPOST').mockReturnValue(of(mockUserOperatorDTO));

    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalledWith(['../success'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });

  it('should create an invited user with credentials', () => {
    store.setState({
      isInvited: true,
      userRegistrationDTO: mockUserOperatorDTO,
      password: mockUserRegistrationDTO.password,
      token: mockUserRegistrationDTO.emailToken,
    });

    fixture.detectChanges();

    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    jest
      .spyOn(service, 'registerNewUserFromInvitationWithCredentialsUsingPUT')
      .mockReturnValue(of(mockUserOperatorDTO));
    jest.spyOn(service, 'acceptOperatorInvitationUsingPOST').mockReturnValue(of({ invitationStatus: 'ACCEPTED' }));

    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalledWith(['../success'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });

  it('should create an invited user without credentials', () => {
    store.setState({
      isInvited: true,
      userRegistrationDTO: mockUserOperatorDTO,
      password: mockUserRegistrationDTO.password,
      token: mockUserRegistrationDTO.emailToken,
      invitationStatus: 'PENDING_USER_REGISTRATION_NO_PASSWORD',
    });

    fixture.detectChanges();

    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    jest.spyOn(service, 'registerNewUserFromInvitationUsingPUT').mockReturnValue(of(mockUserOperatorDTO));
    jest.spyOn(service, 'acceptOperatorInvitationUsingPOST').mockReturnValue(of({ invitationStatus: 'ACCEPTED' }));

    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalledWith(['../../invitation'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });
});
