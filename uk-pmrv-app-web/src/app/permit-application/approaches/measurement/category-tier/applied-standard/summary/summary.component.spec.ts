import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { MeasurementModule } from '../../../measurement.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let fixture: ComponentFixture<SummaryComponent>;
  let component: SummaryComponent;
  let page: Page;
  let store: PermitApplicationStore;
  const route = new ActivatedRouteStub({ index: 0 }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT.sourceStreamCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_Applied_Standard',
  });

  class Page extends BasePage<SummaryComponent> {
    get appliedStandardProperties() {
      return this.queryAll<HTMLDListElement>('dl > div > dd');
    }
    get caption() {
      return this.query<HTMLElement>('.govuk-caption-l');
    }
    get changeLink() {
      return this.query<HTMLLinkElement>('h2.govuk-heading-m > a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, MeasurementModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  describe('for source stream category in complete status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT: {
                type: 'MEASUREMENT',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                    appliedStandard: {
                      parameter: 'parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: true,
                      deviationFromAppliedStandardDetails: 'deviationFromAppliedStandardDetails',
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ],
              },
            },
          },
          {
            sourceStreams: [true],
            emissionSources: [true],
            emissionPoints: [true],
            MEASUREMENT_Category: [true],
            MEASUREMENT_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should populate applied standard properties', () => {
      expect(page.caption.textContent.trim()).toEqual('Measurement, 13123124 White Spirit & SBP: Major');
      expect(page.appliedStandardProperties.map((prop) => prop.textContent.trim())).toEqual(
        expect.arrayContaining([
          'parameter',
          'appliedStandard',
          'Yes  deviationFromAppliedStandardDetails',
          'laboratoryName',
          'Yes',
        ]),
      );
    });
  });

  describe('for source stream category in needs review status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT: {
                type: 'MEASUREMENT',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: 'unspecified',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                    appliedStandard: {
                      parameter: 'parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: true,
                      deviationFromAppliedStandardDetails: 'deviationFromAppliedStandardDetails',
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ],
              },
            },
          },
          {
            sourceStreams: [true],
            emissionSources: [true],
            emissionPoints: [true],
            MEASUREMENT_Category: [true],
            MEASUREMENT_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should populate applied standard properties', () => {
      expect(page.caption.textContent.trim()).toEqual('Measurement, UNDEFINED: Major');
      expect(page.appliedStandardProperties.map((prop) => prop.textContent.trim())).toEqual(
        expect.arrayContaining([
          'parameter',
          'appliedStandard',
          'Yes  deviationFromAppliedStandardDetails',
          'laboratoryName',
          'Yes',
        ]),
      );
    });
  });
});
