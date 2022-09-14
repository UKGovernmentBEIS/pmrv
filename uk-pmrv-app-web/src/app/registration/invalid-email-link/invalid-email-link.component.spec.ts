import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { GovukComponentsModule } from 'govuk-components';

import { ActivatedRouteSnapshotStub } from '../../../testing';
import { InvalidEmailLinkComponent } from './invalid-email-link.component';

describe('InvalidEmailLinkComponent', () => {
  let component: InvalidEmailLinkComponent;
  let fixture: ComponentFixture<InvalidEmailLinkComponent>;
  let route: ActivatedRoute;
  let keycloakService: KeycloakService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, RouterTestingModule],
      declarations: [InvalidEmailLinkComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvalidEmailLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    route = TestBed.inject(ActivatedRoute);
    keycloakService = TestBed.inject(KeycloakService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain email error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'EMAIL1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain(
      'The email verification link has expired',
    );
  });

  it('should contain user error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'USER1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain(
      'The email address has already been registered',
    );
  });

  it('should contain token error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'TOKEN1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain('Invalid token');
  });

  it('should contain form error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'FORM1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain('Form validation failed');
  });

  it('should contain default error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: null });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain('Invalid token');
  });

  it('should sign in on anchor click', () => {
    const signInSpy = jest.spyOn(keycloakService, 'login');
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'USER1001' });

    component.ngOnInit();
    fixture.detectChanges();

    const anchors = fixture.nativeElement.querySelectorAll('a');

    anchors[1].click();

    expect(signInSpy).toHaveBeenCalled();
  });
});
