import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationModule } from '../../../calculation.module';
import { MeteringUncertaintyHelpComponent } from './metering-uncertainty-help.component';

describe('MeteringUncertaintyHelpComponent', () => {
  let component: MeteringUncertaintyHelpComponent;
  let fixture: ComponentFixture<MeteringUncertaintyHelpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(MeteringUncertaintyHelpComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
