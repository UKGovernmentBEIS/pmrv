import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../testing';
import { RfiStore } from '../store/rfi.store';
import { ReturnLinkComponent } from './return-link.component';

describe('ReturnLinkComponent', () => {
  let page: Page;
  let component: ReturnLinkComponent;
  let fixture: ComponentFixture<ReturnLinkComponent>;
  let store: RfiStore;

  class Page extends BasePage<ReturnLinkComponent> {
    get link() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  const route = new ActivatedRouteStub({ taskId: '10' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReturnLinkComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RfiStore);
    store.setState({
      ...store.getState(),
      requestType: 'PERMIT_SURRENDER',
    });
    fixture = TestBed.createComponent(ReturnLinkComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display permit surrender link', () => {
    fixture.detectChanges();

    expect(page.link.textContent.trim()).toEqual('Return to: Permit surrender determination');
    expect(page.link.href).toContain('permit-surrender');
  });
});
