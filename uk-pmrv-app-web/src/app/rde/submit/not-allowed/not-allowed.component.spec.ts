import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestItemsService } from 'pmrv-api';

import { RdeStore } from '../../store/rde.store';
import { NotAllowedComponent } from './not-allowed.component';

describe('NotAllowedComponent', () => {
  let component: NotAllowedComponent;
  let fixture: ComponentFixture<NotAllowedComponent>;

  let page: Page;
  let store: RdeStore;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  const requestItemsService = mockClass(RequestItemsService);

  class Page extends BasePage<NotAllowedComponent> {
    get answers() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotAllowedComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(NotAllowedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    store = TestBed.inject(RdeStore);
    router = TestBed.inject(Router);
    store.setState({
      ...store.getState(),
      requestId: '1',
    });
  };

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should redirect to rde action', () => {
    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        items: [
          {
            taskId: 1,
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE',
            creationDate: '2022-02-02T17:44:52.944926Z',
          },
          {
            taskId: 2,
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE',
            creationDate: '2023-02-02T17:44:52.944926Z',
          },
        ],
      }),
    );
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.confirmButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/rde', 2, 'manual-approval']);
  });
});
