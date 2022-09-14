import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermitApplicationModule } from '../../permit-application.module';
import { ApproachesPrepareTemplateComponent } from './approaches-prepare-template.component';

describe(ApproachesPrepareTemplateComponent, () => {
  let component: ApproachesPrepareTemplateComponent;
  let fixture: ComponentFixture<ApproachesPrepareTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ApproachesPrepareTemplateComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
