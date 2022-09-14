import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationModule } from '../../../calculation.module';
import { ActivityDataHelpComponent } from './activity-data-help.component';

describe('ActivityDataHelpComponent', () => {
  let component: ActivityDataHelpComponent;
  let fixture: ComponentFixture<ActivityDataHelpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ActivityDataHelpComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
