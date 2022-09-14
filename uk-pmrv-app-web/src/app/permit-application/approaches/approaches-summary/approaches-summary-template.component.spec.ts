import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitApplicationModule } from '../../permit-application.module';
import { ApproachesSummaryTemplateComponent } from './approaches-summary-template.component';

describe('ApproachesSummaryTemplateComponent', () => {
  let component: ApproachesSummaryTemplateComponent;
  let fixture: ComponentFixture<ApproachesSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApproachesSummaryTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
