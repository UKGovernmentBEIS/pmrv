import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeasurementModule } from '../measurement.module';
import { HintComponent } from './hint.component';

describe('HintComponent', () => {
  let component: HintComponent;
  let fixture: ComponentFixture<HintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeasurementModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(HintComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
