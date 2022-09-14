import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationModule } from '../../calculation.module';
import { CalculationApproachHelpComponent } from './calculation-approach-help.component';

describe('CalculationApproachHelpComponent', () => {
  let component: CalculationApproachHelpComponent;
  let fixture: ComponentFixture<CalculationApproachHelpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CalculationApproachHelpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
