import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitApplicationModule } from '../../../permit-application/permit-application.module';
import { ApproachesTemplateComponent } from './approaches-template.component';

describe('ApproachesTemplateComponent', () => {
  let component: ApproachesTemplateComponent;
  let fixture: ComponentFixture<ApproachesTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ApproachesTemplateComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
