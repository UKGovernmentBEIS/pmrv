import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { sourceStreamsPayload } from '../../testing/mock-permit-apply-action';
import { SourceStreamsSummaryComponent } from './source-streams-summary.component';

describe('SourceStreamsSummaryComponent', () => {
  let component: SourceStreamsSummaryComponent;
  let fixture: ComponentFixture<SourceStreamsSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<SourceStreamsSummaryComponent> {
    get sourceStreams() {
      return this.queryAll<HTMLDListElement>('dl').map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);

    store.setState({
      ...store.getState(),
      permit: {
        ...store.permit,
        sourceStreams: sourceStreamsPayload,
      },
      requestTaskId: 279,
    });
    fixture = TestBed.createComponent(SourceStreamsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.sourceStreams).toEqual([
      ['13123124 White Spirit & SBP', 'Refineries: Hydrogen production'],
      ['33334 Lignite', 'Refineries: Catalytic cracker regeneration'],
      ['33334 Other Description', 'Refineries: Catalytic cracker regeneration'],
    ]);
  });
});
