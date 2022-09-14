import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { MeasurementDevicesSummaryComponent } from './measurement-devices-summary.component';

describe('MeasurementDevicesSummaryComponent', () => {
  let component: MeasurementDevicesSummaryComponent;
  let fixture: ComponentFixture<MeasurementDevicesSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<MeasurementDevicesSummaryComponent> {
    get measurementDevices() {
      return this.queryAll<HTMLDListElement>('dl').map((measurementDevice) =>
        Array.from(measurementDevice.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(MeasurementDevicesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.measurementDevices).toEqual([
      ['ref1', 'Ultrasonic meter', '3', 'litres', '± 2 %', 'north terminal'],
      ['ref2', 'Ultrasonic meter', '3', 'litres', 'None', 'north terminal'],
    ]);
  });
});
