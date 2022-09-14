import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { InvalidInvitationLinkComponent } from './invalid-invitation-link.component';

describe('InvalidInvitationLinkComponent', () => {
  let component: InvalidInvitationLinkComponent;
  let fixture: ComponentFixture<InvalidInvitationLinkComponent>;
  let activatedRoute: ActivatedRouteStub;
  let element: HTMLElement;

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub();

    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [InvalidInvitationLinkComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvalidInvitationLinkComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display expired link message if code is EMAIL1001', () => {
    activatedRoute.setQueryParamMap({ code: 'EMAIL1001' });
    fixture.detectChanges();

    expect(element.querySelector('h1').textContent).toEqual('This link has expired');
  });

  it('should display invalid link message on any non expired code', () => {
    activatedRoute.setQueryParamMap({ code: 'TOKEN1001' });
    fixture.detectChanges();

    expect(element.querySelector('h1').textContent).toEqual('This link is invalid');
  });
});
