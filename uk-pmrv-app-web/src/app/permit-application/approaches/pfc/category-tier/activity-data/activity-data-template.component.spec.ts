import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PFCModule } from '../../pfc.module';
import { ActivityDataTemplateComponent } from './activity-data-template.component';

describe('ActivityDataTemplateComponent', () => {
  let component: ActivityDataTemplateComponent;
  let fixture: ComponentFixture<ActivityDataTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivityDataTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
