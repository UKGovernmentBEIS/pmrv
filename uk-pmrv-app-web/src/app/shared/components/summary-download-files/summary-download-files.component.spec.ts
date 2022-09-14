import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationStore } from '../../../permit-application/store/permit-application.store';
import { mockState } from '../../../permit-application/testing/mock-state';
import { PermitSurrenderStore } from '../../../permit-surrender/store/permit-surrender.store';
import { initialState } from '../../../rfi/store/rfi.state';
import { RfiStore } from '../../../rfi/store/rfi.store';
import { SharedModule } from '../../shared.module';
import { SummaryDownloadFilesComponent } from './summary-download-files.component';

describe('SummaryDownloadFilesComponent', () => {
  let component: SummaryDownloadFilesComponent;
  let fixture: ComponentFixture<SummaryDownloadFilesComponent>;
  let page: Page;

  class Page extends BasePage<SummaryDownloadFilesComponent> {
    get items() {
      return this.queryAll('a').map((el) => [el.href?.trim(), el.textContent?.trim()]);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  describe('for permit files', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SummaryDownloadFilesComponent);
      component = fixture.componentInstance;
      const store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);

      component.files = store.getDownloadUrlFiles([
        '2c30c8bf-3d5e-474d-98a0-123a87eb60dd',
        '60fe9548-ac65-492a-b057-60033b0fbbea',
      ]);

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display files', () => {
      expect(page.items.length).toEqual(2);
      expect(page.items).toEqual([
        ['http://localhost/permit-application/237/file-download/2c30c8bf-3d5e-474d-98a0-123a87eb60dd', 'cover.jpg'],
        [
          'http://localhost/permit-application/237/file-download/60fe9548-ac65-492a-b057-60033b0fbbea',
          'PublicationAgreement.pdf',
        ],
      ]);
    });
  });

  describe('for permit action files', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SummaryDownloadFilesComponent);
      component = fixture.componentInstance;
      const store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        requestActionType: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED',
        actionId: 1,
      });

      component.files = store.getDownloadUrlFiles([
        '2c30c8bf-3d5e-474d-98a0-123a87eb60dd',
        '60fe9548-ac65-492a-b057-60033b0fbbea',
      ]);

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display files', () => {
      expect(page.items.length).toEqual(2);
      expect(page.items).toEqual([
        [
          'http://localhost/permit-application/action/1/file-download/2c30c8bf-3d5e-474d-98a0-123a87eb60dd',
          'cover.jpg',
        ],
        [
          'http://localhost/permit-application/action/1/file-download/60fe9548-ac65-492a-b057-60033b0fbbea',
          'PublicationAgreement.pdf',
        ],
      ]);
    });
  });

  describe('for rfi files', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SummaryDownloadFilesComponent);
      component = fixture.componentInstance;
      const store = TestBed.inject(RfiStore);
      store.setState({
        ...initialState,
        requestTaskId: 237,
        isEditable: true,
        rfiAttachments: {
          '2c30c8bf-3d5e-474d-98a0-123a87eb60dd': 'cover.jpg',
          '60fe9548-ac65-492a-b057-60033b0fbbea': 'PublicationAgreement.pdf',
          '6e52fd76-4416-4f1f-b7c6-c07b3231d075': 'samplingPlan.pdf',
          '743ba2e6-35c1-4181-b2fd-6a648361d691': 'uncertainty.pdf',
        },
      });

      component.files = store.getDownloadUrlFiles([
        '2c30c8bf-3d5e-474d-98a0-123a87eb60dd',
        '60fe9548-ac65-492a-b057-60033b0fbbea',
      ]);

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display files', () => {
      expect(page.items.length).toEqual(2);
      expect(page.items).toEqual([
        ['http://localhost/rfi/237/file-download/2c30c8bf-3d5e-474d-98a0-123a87eb60dd', 'cover.jpg'],
        ['http://localhost/rfi/237/file-download/60fe9548-ac65-492a-b057-60033b0fbbea', 'PublicationAgreement.pdf'],
      ]);
    });
  });

  describe('for rfi action files', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SummaryDownloadFilesComponent);
      component = fixture.componentInstance;
      const store = TestBed.inject(RfiStore);
      store.setState({
        ...initialState,
        requestTaskId: 237,
        isEditable: false,
        actionId: 1,
        rfiAttachments: {
          '2c30c8bf-3d5e-474d-98a0-123a87eb60dd': 'cover.jpg',
          '60fe9548-ac65-492a-b057-60033b0fbbea': 'PublicationAgreement.pdf',
          '6e52fd76-4416-4f1f-b7c6-c07b3231d075': 'samplingPlan.pdf',
          '743ba2e6-35c1-4181-b2fd-6a648361d691': 'uncertainty.pdf',
        },
      });

      component.files = store.getDownloadUrlFiles([
        '2c30c8bf-3d5e-474d-98a0-123a87eb60dd',
        '60fe9548-ac65-492a-b057-60033b0fbbea',
      ]);

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display files', () => {
      expect(page.items.length).toEqual(2);
      expect(page.items).toEqual([
        ['http://localhost/rfi/action/1/file-download/attachment/2c30c8bf-3d5e-474d-98a0-123a87eb60dd', 'cover.jpg'],
        [
          'http://localhost/rfi/action/1/file-download/attachment/60fe9548-ac65-492a-b057-60033b0fbbea',
          'PublicationAgreement.pdf',
        ],
      ]);
    });
  });

  describe('for surrender files', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SummaryDownloadFilesComponent);
      component = fixture.componentInstance;
      const store = TestBed.inject(PermitSurrenderStore);
      store.setState({
        ...store.getState(),
        requestTaskId: mockState.requestTaskId,
        isEditable: true,
        permitSurrender: {
          stopDate: '2012-12-21',
          justification: 'justify',
          documentsExist: true,
          documents: ['e227ea8a-778b-4208-9545-e108ea66c114'],
        },
        permitSurrenderAttachments: { 'e227ea8a-778b-4208-9545-e108ea66c114': 'hello.txt' },
        sectionsCompleted: { SURRENDER_APPLY: false },
      });

      component.files = store.getDownloadUrlFiles(['e227ea8a-778b-4208-9545-e108ea66c114']);

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display files', () => {
      expect(page.items.length).toEqual(1);
      expect(page.items).toEqual([
        ['http://localhost/permit-surrender/237/file-download/e227ea8a-778b-4208-9545-e108ea66c114', 'hello.txt'],
      ]);
    });
  });

  describe('for empty files', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(SummaryDownloadFilesComponent);
      component = fixture.componentInstance;
      const store = TestBed.inject(PermitSurrenderStore);
      store.setState({
        ...store.getState(),
        requestTaskId: mockState.requestTaskId,
        isEditable: true,
        permitSurrender: {
          stopDate: '2012-12-21',
          justification: 'justify',
          documentsExist: true,
          documents: [],
        },
        permitSurrenderAttachments: { 'e227ea8a-778b-4208-9545-e108ea66c114': 'hello.txt' },
        sectionsCompleted: { SURRENDER_APPLY: false },
      });

      component.files = store.getDownloadUrlFiles([]);

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should display files', () => {
      expect(page.items.length).toEqual(0);
      expect(page.items).toEqual([]);
    });
  });
});
