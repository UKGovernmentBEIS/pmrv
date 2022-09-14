import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { setStoreTask } from '../../testing/set-store-task';
import { SiteDiagramSummaryComponent } from './site-diagram-summary.component';

describe('SiteDiagramSummaryComponent', () => {
  let component: SiteDiagramSummaryComponent;
  let fixture: ComponentFixture<SiteDiagramSummaryComponent>;
  let page: Page;

  class Page extends BasePage<SiteDiagramSummaryComponent> {
    get file() {
      const roles = this.queryAll<HTMLDListElement>('dd');

      return roles.slice(roles.length - 1)[0];
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule],
    }).compileComponents();

    const store = TestBed.inject(PermitApplicationStore);
    store.setState({ ...store.getState(), permitAttachments: { abc: 'some-file.txt' } });
    setStoreTask('siteDiagrams', mockPermitApplyPayload.permit.siteDiagrams, [true]);
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);

    fixture = TestBed.createComponent(SiteDiagramSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mention the attachment', () => {
    expect(page.file.textContent.trim()).toEqual('some-file.txt');
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
