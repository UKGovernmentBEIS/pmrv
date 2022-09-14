import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukComponentsModule } from 'govuk-components';

import { ActivatedRouteStub } from '../../../testing';
import { InvitationComponent } from './invitation.component';

describe('InvitationComponent', () => {
  let component: InvitationComponent;
  let fixture: ComponentFixture<InvitationComponent>;
  let element: HTMLElement;
  const activatedRoute = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, RouterTestingModule],
      declarations: [InvitationComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvitationComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the installation name for emitter contacts', () => {
    const installation = {
      accountInstallationName: 'Test Faculty',
      roleCode: 'emitter_contact',
    };

    activatedRoute.setResolveMap({ installation });
    fixture.detectChanges();

    expect(element.querySelector('.govuk-panel__title').textContent).toEqual(
      `You have been added as an emitter contact at installation Test Faculty`,
    );
  });

  it('should display the installation name for operators', () => {
    const installation = {
      accountInstallationName: 'Operator Faculty',
      roleCode: 'operator',
    };

    activatedRoute.setResolveMap({ installation });
    fixture.detectChanges();

    expect(element.querySelector('.govuk-panel__title').textContent).toEqual(
      `You have been added as an operator at installation Operator Faculty`,
    );
  });

  it('should have a link towards dashboard', () => {
    expect(element.querySelector('a').href).toMatch(/\/dashboard$/);
  });
});
