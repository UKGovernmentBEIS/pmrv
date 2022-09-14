import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { monitoringRolesPayload } from '../../testing/mock-permit-apply-action';
import { setStoreTask } from '../../testing/set-store-task';
import { MonitoringRolesSummaryComponent } from './monitoring-roles-summary.component';

describe('MonitoringRolesSummaryComponent', () => {
  let component: MonitoringRolesSummaryComponent;
  let fixture: ComponentFixture<MonitoringRolesSummaryComponent>;
  let page: Page;

  class Page extends BasePage<MonitoringRolesSummaryComponent> {
    get roles() {
      const roles = this.queryAll<HTMLDListElement>('dl');

      return roles.slice(0, roles.length - 1);
    }
    get files() {
      const roles = this.queryAll<HTMLDListElement>('dl dd');

      return roles.slice(roles.length - 1);
    }
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
    }).compileComponents();

    const store = TestBed.inject(PermitApplicationStore);
    store.setState({ ...store.getState(), permitAttachments: { abc: 'some-file.txt' } });
    setStoreTask('monitoringReporting', monitoringRolesPayload, [true]);
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);

    fixture = TestBed.createComponent(MonitoringRolesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list the roles', () => {
    expect(page.roles.map((permit) => Array.from(permit.querySelectorAll('dd')).map((dd) => dd.textContent))).toEqual([
      ['Test job', 'Testing'],
      ['Check job', 'Checking'],
    ]);
  });

  it('should mention the attachment', () => {
    expect(page.files.map((dd) => dd.textContent)).toEqual(['some-file.txt']);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
