import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { KeycloakProfile } from 'keycloak-js';

import { GovukComponentsModule } from 'govuk-components';

import { AuthService } from '../../core/services/auth.service';
import { PhaseBarComponent } from './phase-bar.component';

describe('PhaseBarComponent', () => {
  let component: PhaseBarComponent;
  let fixture: ComponentFixture<PhaseBarComponent>;
  const authService: Partial<jest.Mocked<AuthService>> = {
    userProfile: new BehaviorSubject<KeycloakProfile>({ firstName: 'Gimli', lastName: 'Gloin' }),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PhaseBarComponent],
      imports: [GovukComponentsModule, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PhaseBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
