import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { FallbackComponent } from './fallback.component';
import { FallbackModule } from './fallback.module';

describe('FallbackComponent', () => {
  let page: Page;
  let component: FallbackComponent;
  let fixture: ComponentFixture<FallbackComponent>;
  let store: PermitApplicationStore;

  class Page extends BasePage<FallbackComponent> {
    get subTasks(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li'));
    }
    get subTaskDescriptions() {
      return Array.from(this.subTasks).map((li) => li.querySelector<HTMLSpanElement>('span').textContent.trim());
    }
    getSubTaskStatus(value) {
      return Array.from(this.subTasks)
        .find((li) => li.querySelector<HTMLSpanElement>('span').textContent.trim() === value)
        .querySelector<HTMLElement>('govuk-tag strong')
        .textContent.trim();
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FallbackModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild({
        ...mockPermitApplyPayload.permit,
        monitoringApproaches: {
          FALLBACK: {},
        },
      }),
    );

    fixture = TestBed.createComponent(FallbackComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display list of sub tasks', () => {
    expect(page.rows).toEqual([['Fall-back', '0t', '0t', '0t', '0t']]);
    expect(page.subTaskDescriptions).toEqual([
      'Add a source stream category',
      'Approach description and justification',
      'Annual uncertainty analysis',
    ]);
    expect(page.getSubTaskStatus('Add a source stream category')).toEqual('cannot start yet');
    expect(page.getSubTaskStatus('Approach description and justification')).toEqual('not started');
    expect(page.getSubTaskStatus('Annual uncertainty analysis')).toEqual('not started');
  });
});
