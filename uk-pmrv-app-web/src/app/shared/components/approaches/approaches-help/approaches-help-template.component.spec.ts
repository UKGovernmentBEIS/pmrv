import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { ApproachesHelpTemplateComponent } from './approaches-help-template.component';

describe('ApproachesHelpTemplateComponent', () => {
  let component: ApproachesHelpTemplateComponent;
  let fixture: ComponentFixture<ApproachesHelpTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ApproachesHelpTemplateComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
