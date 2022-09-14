import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { TransferredCO2Module } from '../../transferred-co2.module';
import { SummaryTemplateComponent } from './summary-template.component';

describe('SummaryTemplateComponent', () => {
  let component: SummaryTemplateComponent;
  let fixture: ComponentFixture<SummaryTemplateComponent>;
  let page: Page;
  let store: PermitApplicationStore;
  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub();

  class Page extends BasePage<SummaryTemplateComponent> {
    get installations() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get changeLink() {
      return this.query<HTMLLinkElement>('h2.govuk-heading-m > a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferredCO2Module, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {},
        {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          TRANSFERRED_CO2_Installations: [true],
        },
      ),
    );

    fixture = TestBed.createComponent(SummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of installations', () => {
    expect(page.installations).toEqual([['Receiving installation', 'code1', 'operator1', 'name1', 'source1']]);
  });
});
