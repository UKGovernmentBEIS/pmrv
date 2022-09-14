import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let store: InstallationAccountApplicationStore;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [InstallationAccountApplicationStore, { provide: TasksService, useValue: {} }],
    });
    router = TestBed.inject(Router);
    guard = TestBed.inject(SummaryGuard);
    store = TestBed.inject(InstallationAccountApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();

    await lastValueFrom(guard.canActivate());
    expect(navigateSpy).toHaveBeenCalledWith('');
  });

  it('should activate if data exist', async () => {
    store.updateTask(ApplicationSectionType.responsibility, null, 'complete');
    store.updateTask(ApplicationSectionType.legalEntity, null, 'complete');
    store.updateTask(ApplicationSectionType.installation, null, 'complete');
    store.updateTask(ApplicationSectionType.etsScheme, null, 'complete');
    store.updateTask(ApplicationSectionType.commencement, null, 'complete');
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();

    const res = await lastValueFrom(guard.canActivate());
    expect(navigateSpy).not.toHaveBeenCalled();
    expect(res).toBeTruthy();
  });
});
