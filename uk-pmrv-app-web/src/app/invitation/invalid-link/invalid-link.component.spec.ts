import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukComponentsModule } from 'govuk-components';

import { ActivatedRouteStub } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { InvalidLinkComponent } from './invalid-link.component';

describe('InvalidLinkComponent', () => {
  let component: InvalidLinkComponent;
  let fixture: ComponentFixture<InvalidLinkComponent>;
  let element: HTMLElement;

  const activatedRoute = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, RouterTestingModule, SharedModule],
      declarations: [InvalidLinkComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvalidLinkComponent);
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
