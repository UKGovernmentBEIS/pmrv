import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { RouterStubComponent } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { ConcurrencyError } from './concurrency-error';
import { ConcurrencyErrorComponent } from './concurrency-error.component';
import { ConcurrencyErrorService } from './concurrency-error.service';

describe('ConcurrencyErrorComponent', () => {
  let component: ConcurrencyErrorComponent;
  let fixture: ComponentFixture<ConcurrencyErrorComponent>;

  const error = new ConcurrencyError('Test header').withLink({ link: ['/dashboard'], linkText: 'Go to dashboard' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RouterTestingModule.withRoutes([
          { path: 'dashboard', component: RouterStubComponent },
          { path: 'error/concurrency', component: ConcurrencyErrorComponent },
        ]),
      ],
      declarations: [ConcurrencyErrorComponent, RouterStubComponent],
    }).compileComponents();

    TestBed.inject(ConcurrencyErrorService).showError(error);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConcurrencyErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the heading and link of the latest concurrency error', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('h1').textContent).toEqual('Test header');
    expect(element.querySelector('a').textContent).toEqual('Go to dashboard');
    expect(element.querySelector('a').href).toMatch(/\/dashboard$/);
  });

  it('should clear the error on destroy', async () => {
    fixture.destroy();

    await expect(firstValueFrom(TestBed.inject(ConcurrencyErrorService).error$)).resolves.toBeNull();
  });
});
